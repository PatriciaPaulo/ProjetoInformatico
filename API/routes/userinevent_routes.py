
from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot,GarbageSpotInEvent,UserInEvent,GarbageInEvent,Garbage
from utils import token_required,admin_required,guest
import jwt
from datetime import datetime

users_event_routes_blueprint = Blueprint('users_event_routes', __name__, )
api = Api(users_event_routes_blueprint)



# Get All Events by User
@users_event_routes_blueprint.route('/events/<event_id>/usersinevent', methods=['GET'])
@token_required
def get_user_in_event(current_user,event_id):
    #todo order by date

    users_events = db.session.query(UserInEvent).filter_by(eventID=event_id).all()

    output = []

    for event in users_events:
        print(event)
        event_data = {}
        event_data['id'] = event.id
        event_data['userID'] = event.userID
        event_data['event'] = {}
        for ev in db.session.query(Event).filter_by(id=event.eventID):
            event_data['event'] = Event.serialize(ev)

        event_data['status'] = event.status
        event_data['creator'] = event.creator
        output.append(event_data)
    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Event Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)


# Add user to Event by User
@users_event_routes_blueprint.route('/events/<event_id>/signUpEvent', methods=['POST'])
@token_required
def add_user_to_event(current_user,event_id):
   # print("Event - "+event_id)
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'data': {},'message': '404 NOT OK - No Event Found'}), 404)


    user_event = db.session.query(UserInEvent).filter_by(userID=current_user.id,eventID=event_id).first()
    if user_event:
        return make_response(jsonify({'data': {},'message': '409 NOT OK - Already signed up to Event! Update your status instead!'}), 409)

    signUp = UserInEvent(userID=current_user.id, eventID=event_id,status="Inscrito",creator = False)

    db.session.add(signUp)
    db.session.commit()


    return make_response(jsonify({'data': current_user.id, 'message': '200 OK - User Sign Up to Event'}), 200)


# Add user to Event by Organizer
@users_event_routes_blueprint.route('/events/<event_id>/signUpEventOrganizer', methods=['POST'])
@token_required
def add_organizer_to_event(current_user,event_id):
   # print("Event - "+event_id)
    data =  request.get_json()
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'data': {},'message': '404 NOT OK - No Event Found'}), 404)

    if event.status != "Criado":
        return make_response(jsonify({'data': {},'message': '404 NOT OK - Event status doesn\'t allow action'}), 400)

    user_event = db.session.query(UserInEvent).filter_by(userID=data,eventID=event_id).first()
    if user_event:
        return make_response(jsonify({'data': {},'message': '409 NOT OK - Already signed up to Event! Update your status instead!'}), 409)

    signUp = UserInEvent(userID=data, eventID=event_id,status="Organizer",creator = False)

    db.session.add(signUp)
    db.session.commit()

    return make_response(jsonify({'data': data,'message': '200 OK - User Sign Up to Event'}), 200)

@users_event_routes_blueprint.route('/events/<event_id>/signUpUpdateStatusEvent/<user_event_id>', methods=['Patch'])
@token_required
def update_status_user_to_event(current_user,event_id,user_event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'data': {},'message': '404 NOT OK - No Event Found'}), 404)

    user_event = db.session.query(UserInEvent).filter_by(id=user_event_id).first()
    if not user_event:
        return make_response(jsonify({'data': {},'message': '404 NOT OK - User In Event Not Found! '}), 404)

    data = request.get_json()
    print(data)
    user_event.status = data

    db.session.commit()

    return make_response(jsonify({'data': user_event.userID,'message': '200 OK - User Sign Up to Event Status Updated'}), 200)
# endregion