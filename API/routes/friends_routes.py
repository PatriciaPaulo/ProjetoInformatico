from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, Friendship,UserInEvent, GarbageSpot
from utils import token_required, admin_required, guest, name_validation, username_validation, email_validation,  password_validation, password_confirmation
import jwt
from datetime import datetime

friends_routes_blueprint = Blueprint('friends_routes', __name__, )
api = Api(friends_routes_blueprint)


# Register New friend
@friends_routes_blueprint.route('/friends', methods=['POST'])
@token_required
def friend_request(current_user):
    #receives user id that logged in user sent / received friend request from
    data = request.get_json()

    # check if user in friendship with logged in user exists
    user = db.session.query(User).filter_by(id=data).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

    if user.admin:
        return make_response(jsonify({'message': '404 NOT OK - User is an Admin!'}), 404)
    if user.blocked:
        return make_response(jsonify({'message': '404 NOT OK - User is blocked!'}), 404)

    #checks if friendship already exists where logged in user sent request
    friendNewRequest = db.session.query(Friendship).filter_by(requestorID=current_user.id,addresseeID=data).first()


    #if doesnt
    if not friendNewRequest:
        #check if friendship already exists where other user is the requests
        friendImAddressee = db.session.query(Friendship).filter_by(addresseeID=current_user.id, requestorID=data,status="Pendente").first()
        today = datetime.utcnow()
        #if yes complete request
        if friendImAddressee:
            friendImAddressee.status = "Completo"
            friendImAddressee.completeDate = today
            db.session.commit()
            return make_response(jsonify({'message': '200 OK - Friend request accepted'}), 202)

        # if not create new one
        else:

            new_friendship = Friendship(requestorID=current_user.id, addresseeID=data, completeDate=None, status="Pendente")
            db.session.add(new_friendship)
            db.session.commit()
            return make_response(jsonify({'message': '200 OK - Friend request sent'}), 200)

    # if exists return error
    elif friendNewRequest.status == "Pendente":
        return make_response(jsonify({'message': '409 NOT OK - You are already sent friend request to this user!'}), 409)
    elif friendNewRequest.status == "Completo":
        return make_response(jsonify({'message': '409 NOT OK - You are already friends with this user!'}), 409)


# Get users and stats by user
@friends_routes_blueprint.route('/users/<user_id>/friend', methods=['GET'])
@token_required
def get_friendship(current_user,user_id):

    #check if user exists
    user = db.session.query(User).filter_by(id=user_id).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

    #check if friendship exists where logged in user is the requestor
    friendAddressee =  db.session.query(Friendship).filter_by(requestorID=current_user.id,addresseeID=user.id).first()

    # if friendship doesnt exists where logged in user is the requestor
    if not friendAddressee:
        friendRequestor =  db.session.query(Friendship).filter_by(addresseeID=current_user.id,requestorID=user.id).first()
        # if friendship doesnt exists where logged in user is the addressee
        if not friendRequestor:
            return make_response(jsonify({'message': '404 NOT OK - You are not friends with this User!'}), 404)

        #if request from user isnt completed by logged in user
        if friendRequestor.status != "Completo":
            return make_response(jsonify({'message': '404 NOT OK - Friendship Pending!You need to accept request!'}), 404)

    #if requests exists but isnt completed on other users end
    elif friendAddressee.status != "Completo":
        return make_response(jsonify({'message': '404 NOT OK - Friendship Pending! Other user needs to accept!'}), 404)

    #if friendship completed
    return make_response(jsonify({'message': '200 OK - Friends with User'}), 200)



# Get all logged in users friends
@friends_routes_blueprint.route('/friends/me', methods=['GET'])
@token_required
def get_user_friends(current_user):

    result = []
    friendsReq = db.session.query(Friendship).filter_by(requestorID=current_user.id,status="Completo").all()

    for friend in friendsReq:
        friend_data = {}
        friend_data['id'] = friend.id


        user = db.session.query(User).filter_by(id=friend.id).first()
        if not user.admin:
            if not user.blocked:
                events_participated = db.session.query(UserInEvent).filter_by(userID=user.id,
                                                                              status="Confirmado").count()
                # print(events_participated)
                from datetime import date

                today = date.today()
                activities_completed = db.session.query(Activity).filter(Activity.userID == user.id,
                                                                         Activity.endDate <= today).count()

                garbage_spots_created = db.session.query(GarbageSpot).filter(GarbageSpot.creator == user.id).count()

                user_data = {}
                user_data['id'] = user.id
                user_data['username'] = user.username
                user_data['name'] = user.name
                user_data['events_participated'] = events_participated
                user_data['activities_completed'] = activities_completed
                user_data['garbage_spots_created'] = garbage_spots_created

        friend_data['user'] = user_data
        friend_data['status'] = friend.status
        friend_data['date'] = friend.date

        result.append(friend_data)


    friendsAddr = db.session.query(Friendship).filter_by(addresseeID=current_user.id, status="Completo").all()
    for friend in friendsAddr:
        friend_data = {}
        friend_data['id'] = friend.id

        user = db.session.query(User).filter_by(id=friend.id).first()
        if not user.admin:
            if not user.blocked:
                events_participated = db.session.query(UserInEvent).filter_by(userID=user.id,
                                                                              status="Confirmado").count()
                # print(events_participated)
                from datetime import date

                today = date.today()
                activities_completed = db.session.query(Activity).filter(Activity.userID == user.id,
                                                                         Activity.endDate <= today).count()

                garbage_spots_created = db.session.query(GarbageSpot).filter(GarbageSpot.creator == user.id).count()

                user_data = {}
                user_data['id'] = user.id
                user_data['username'] = user.username
                user_data['name'] = user.name
                user_data['events_participated'] = events_participated
                user_data['activities_completed'] = activities_completed
                user_data['garbage_spots_created'] = garbage_spots_created

        friend_data['user'] = user_data
        friend_data['status'] = friend.status
        friend_data['date'] = friend.date

        result.append(friend_data)
    #if friendship completed
    print(result)
    return make_response(jsonify({'data':result,'message': '200 OK - All Friends Retrieved'}), 200)
