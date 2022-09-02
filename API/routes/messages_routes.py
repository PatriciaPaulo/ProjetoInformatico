from flask import jsonify, make_response, request, current_app, Flask, json
from sqlalchemy import or_, and_, desc, asc
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, Friendship, UserInEvent, GarbageSpot, Message, IndividualMessage, \
    EventMessage
from utils import token_required, admin_required, guest, name_validation, username_validation, email_validation, \
    password_validation, password_confirmation
import jwt
from datetime import datetime
from websockets_server import users_connected, send_notification_event
from websockets_server import send_notification_user

messages_routes_blueprint = Blueprint('messages_routes', __name__, )
api = Api(messages_routes_blueprint)


# new message
@messages_routes_blueprint.route('/messages', methods=['POST'])
@token_required
def send_message(current_user):
    # receives message content
    data = request.get_json()

    # check if user in friendship with logged in user exists
    if data["userID"] and data["type"] == "Individual":
        user = db.session.query(User).filter_by(id=data["userID"]).first()
        if not user:
            return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

        if user.admin:
            return make_response(jsonify({'message': '400 NOT OK - User is an Admin!'}), 400)
        if user.blocked:
            return make_response(jsonify({'message': '400 NOT OK - User is blocked!'}), 400)

        if current_user.id == user.id:
            return make_response(jsonify({'message': '400 NOT OK - Cant message yourself!'}), 400)

        message = Message(senderID=current_user.id, message=data['message'], status="Sent", type=data['type'],
                          sentDate=datetime.utcnow())
        db.session.add(message)

        db.session.flush()


        messageInd = IndividualMessage(receiverID=data["userID"], messageID=message.id)
        db.session.add(messageInd)
        send_notification_user(user.id, message)

    db.session.commit()

    return make_response(jsonify({'message': '200 OK - Message sent'}), 200)


# new message
@messages_routes_blueprint.route('/messagesEvent', methods=['POST'])
@token_required
def send_message_event(current_user):
    # receives message content
    data = request.get_json()

    print(data["eventID"])

    if data["eventID"] and data["type"] == "Event":
        event = db.session.query(Event).filter_by(id=data["eventID"]).first()
        if not event:
            return make_response(jsonify({'message': '404 NOT OK - Event Not Found!'}), 404)

        message = Message(senderID=current_user.id, message=data['message'], status="Sent", type=data['type'],
                          sentDate=datetime.utcnow())
        db.session.add(message)

        db.session.flush()

        if message.type == "Event":
            messageEvent = EventMessage(eventID=data["eventID"], messageID=message.id)
            db.session.add(messageEvent)

        db.session.commit()

        send_notification_event(event.id, message, current_user)
        return make_response(jsonify({'message': '200 OK - Message sent'}), 200)

    return make_response(jsonify({'message': '400 NOT OK - Fields missing!'}), 400)


# Get last message with user

@messages_routes_blueprint.route('/friends/lastMessage', methods=['GET'])
@token_required
def get_last_message(current_user):
    # check if user in friendship with logged in user exists
    # print("get last message friends")

    friends = db.session.query(Friendship).filter(
        and_(or_(Friendship.requestorID==current_user.id, Friendship.addresseeID==current_user.id), Friendship.status=="Complete")).all()
    if len(friends) == 0:
        return make_response(jsonify({'data': [], 'message': '404 OK - No friends'}), 404)

    result = []
    for friendData in friends:
        if current_user.id == friendData.requestorID:
            friendID = friendData.addresseeID
        else:
            friendID = friendData.requestorID

        user = db.session.query(User).filter_by(id=friendID).first()

        if user:
            if user is not current_user:
                message = db.session \
                    .query(IndividualMessage, Message) \
                    .join(IndividualMessage, Message.id == IndividualMessage.messageID) \
                    .filter(or_
                            (and_(Message.senderID == user.id,
                                  IndividualMessage.receiverID == current_user.id),
                             (and_(Message.senderID == current_user.id,
                                   IndividualMessage.receiverID == user.id)))).order_by(
                    desc(Message.sentDate)).first()

                if not message:
                    #print("no messages")
                    empty = {"id": 0,
                             "message": "No messages",
                             "status": "",
                             "receiverID": current_user.id,
                             "deliveryDate": None,
                             "senderID": 0}
                    result.append(empty)

                else:
                    message_data = {"id": message.Message.id,
                                    "message": message.Message.message,
                                    "status": message.Message.status,
                                    "receiverID": message.IndividualMessage.receiverID,
                                    "deliveryDate": message.IndividualMessage.deliveryDate,
                                    "senderID": message.Message.senderID}
                    #print("has messages")
                    result.append(message_data)

    return make_response(jsonify({'data': result, 'message': '200 OK - All Last Messages Retrieved'}), 200)


# Get All message with user
@messages_routes_blueprint.route('/friends/<friend_id>/messages', methods=['GET'])
@token_required
def get_all_messages(current_user, friend_id):
    # check if user in friendship with logged in user exists

    friendship = db.session.query(Friendship).filter_by(id=friend_id).first()

    if not friendship:
        return make_response(jsonify({'message': '404 NOT OK - Friendship doenst exist!'}), 404)

    if friendship.status != "Complete":
        return make_response(jsonify({'message': '404 NOT OK - Friendship isnt complete!'}), 404)

    if current_user.id == friendship.requestorID:
        friend = friendship.addresseeID
    else:
        friend = friendship.requestorID

    user = db.session.query(User).filter_by(id=friend).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)
    if user is current_user:
        return make_response(jsonify({'message': '400 NOT OK - No messages with yourself!'}), 400)
    if user.admin:
        return make_response(jsonify({'message': '400 NOT OK - User is an Admin!'}), 400)
    if user.blocked:
        return make_response(jsonify({'message': '400 NOT OK - User is blocked!'}), 400)

    messages = db.session \
        .query(IndividualMessage, Message) \
        .join(IndividualMessage, Message.id == IndividualMessage.messageID) \
        .filter(or_
                (and_(Message.senderID == current_user.id,
                      IndividualMessage.receiverID == user.id),
                 (and_(Message.senderID == user.id,
                       IndividualMessage.receiverID == current_user.id)))).order_by(asc(Message.sentDate))

    output = []

    for message in messages:
        # print(message)
        message_data = {"id": message.Message.id,
                        "message": message.Message.message,
                        "status": message.Message.status,
                        "receiverID": message.IndividualMessage.receiverID,
                        "deliveryDate": message.IndividualMessage.deliveryDate,
                        "senderID": message.Message.senderID}

        output.append(message_data)

    if len(output) == 0:
        empty = [{"id": 0,
                  "message": "No messages",
                  "status": "",
                  "receiverID": 0,
                  "deliveryDate": None,
                  "senderID": 0}]
        return make_response(jsonify({'data': empty, 'message': '200 NOT OK - No Messages Found'}), 200)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Messages Retrieved'}), 200)


# Get last message in event chat
@messages_routes_blueprint.route('/events/lastMessage', methods=['GET'])
@token_required
def get_last_message_from_event_chat(current_user):
    # check if user in friendship with logged in user exists
    #print("get last message events")

    eventsuser = db.session.query(UserInEvent).filter_by(userID=current_user.id).all()
    result = []
    if len(eventsuser) == 0:
        return make_response(jsonify({'data': [], 'message': '404 OK - No events found'}), 404)

    for eventuser in eventsuser:

        event = db.session.query(Event).filter_by(id=eventuser.eventID).first()
        if event:
            message = db.session \
                .query(EventMessage, Message) \
                .join(EventMessage, Message.id == EventMessage.messageID) \
                .filter(EventMessage.eventID == eventuser.eventID).order_by(desc(Message.sentDate)).first()

            if not message:
                #print("no messages")
                empty = {"id": 0,
                         "message": "No messages",
                         "status": "",
                         "receiverID": event.id,
                         "deliveryDate": None,
                         "senderID": 0}
                result.append(empty)

            else:
                message_data = {"id": message.Message.id,
                                "message": message.Message.message,
                                "status": message.Message.status,
                                "receiverID": message.EventMessage.eventID,
                                "deliveryDate": message.EventMessage.deliveryDate,
                                "senderID": message.Message.senderID}
                #print("has messages")
                result.append(message_data)

    return make_response(jsonify({'data': result, 'message': '200 OK - All Messages Retrieved'}), 200)


# Get All message with user
@messages_routes_blueprint.route('/events/<event_id>/messages', methods=['GET'])
@token_required
def get_all_messages_from_event_chat(current_user, event_id):
    # check if user in friendship with logged in user exists

    event = db.session.query(Event).filter_by(id=event_id).first()

    if not event:
        return make_response(jsonify({'message': '404 NOT OK - Event Not Found!'}), 404)

    messages = db.session \
        .query(EventMessage, Message) \
        .join(EventMessage, Message.id == EventMessage.messageID) \
        .filter(EventMessage.eventID == event_id).order_by(asc(Message.sentDate))

    output = []

    for message in messages:
        # print(message)
        message_data = {"id": message.Message.id,
                        "message": message.Message.message,
                        "status": message.Message.status,
                        "receiverID": message.EventMessage.eventID,
                        "deliveryDate": message.EventMessage.deliveryDate,
                        "senderID": message.Message.senderID}

        output.append(message_data)

    if len(output) == 0:
        empty = [{"id": 0,
                  "message": "No messages",
                  "status": "",
                  "receiverID": 0,
                  "deliveryDate": None,
                  "senderID": 0}]
        return make_response(jsonify({'data': empty, 'message': '200 NOT OK - No Messages Found'}), 200)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Messages Retrieved'}), 200)
