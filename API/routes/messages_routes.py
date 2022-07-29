from flask import jsonify, make_response, request, current_app, Flask
from sqlalchemy import or_, and_, desc
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, Friendship, UserInEvent, GarbageSpot, Message, IndividualMessage, \
    EventMessage
from utils import token_required, admin_required, guest, name_validation, username_validation, email_validation, \
    password_validation, password_confirmation
import jwt
from datetime import datetime
from websockets_server import users_connected
from websockets_server import send_notification

messages_routes_blueprint = Blueprint('messages_routes', __name__, )
api = Api(messages_routes_blueprint)


# new message
@messages_routes_blueprint.route('/messages', methods=['POST'])
@token_required
def send_message(current_user):
    # receives message content
    data = request.get_json()

    # check if user in friendship with logged in user exists
    user = db.session.query(User).filter_by(id=data["userID"]).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

    if user.admin:
        return make_response(jsonify({'message': '400 NOT OK - User is an Admin!'}), 400)
    if user.blocked:
        return make_response(jsonify({'message': '400 NOT OK - User is blocked!'}), 400)

    message = Message(senderID=current_user.id, message=data['message'], status="Sent", type=data['type'],
                      sentDate=datetime.utcnow())
    db.session.add(message)

    db.session.flush()

    if message.type == "Individual":
        messageInd = IndividualMessage(receiverID=data["userID"], messageID=message.id)
        db.session.add(messageInd)

    elif message.type == "Event":
        messageEvent = EventMessage(eventID=data["eventID"], messageID=message.id)
        db.session.add(messageEvent)

    db.session.commit()

    send_notification(user.id,message)
    return make_response(jsonify({'message': '200 OK - Message sent'}), 200)



# Get Garbage Spots created by Logged User
@messages_routes_blueprint.route('/friends/<friend_id>/messages', methods=['GET'])
@token_required
def get_all_messages(current_user, friend_id):
    # check if user in friendship with logged in user exists
    user = db.session.query(User).filter_by(id=friend_id).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

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
                       IndividualMessage.receiverID == current_user.id)))).order_by(desc(Message.sentDate))

    output = []

    for message in messages:

        print(message)
        message_data = {"id": message.Message.id, "status": message.Message.status,
                        "receiverID": message.IndividualMessage.receiverID,
                        "deliveryDate": message.IndividualMessage.deliveryDate, "senderID": message.Message.senderID,
                        "sentDate": message.Message.sentDate}

        output.append(message_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Messages Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Messages Retrieved'}), 200)
