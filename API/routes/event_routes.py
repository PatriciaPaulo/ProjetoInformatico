from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot,GarbageSpotInEvent,UserInEvent,GarbageInEvent,Garbage
from utils import token_required,admin_required,guest
import jwt
from datetime import datetime

event_routes_blueprint = Blueprint('event_routes', __name__, )
api = Api(event_routes_blueprint)

# Create Event by User
@event_routes_blueprint.route('/events', methods=['POST'])
@token_required
def create_event(current_user):
    data = request.get_json()
    print(data)
    event = db.session.query(Event).filter_by(latitude=data['event']['latitude']).first()
    if event:
        if event.longitude == float(data['event']['longitude']):
            return make_response(jsonify({'message': '409 NOT OK - Event already exists! Try to sign up as organizer instead!'}),
                                 409)

    start_date_time_obj = datetime.strptime(data['event']['startDate'], '%d/%m/%Y %H:%M')



    new_event = Event(name=data['event']['name'], latitude=data['event']['latitude'], longitude=data['event']['longitude'],
                       status=data['event']['status'], duration=data['event']['duration'], description=data['event']['description'],
                       accessibility=data['event']['accessibility'], restrictions=data['event']['restrictions'],
                       quantity=data['event']['quantity'], observations=data['event']['observations'], startDate=start_date_time_obj,
                       )

    db.session.add(new_event)

    for garbageID in data['garbageList']:
        garbageExists = db.session.query(Garbage).filter_by(id=garbageID).first()
        if garbageExists:
            new_garbageInEvent = GarbageInEvent(eventID=new_event.id, garbageID=garbageID)
            db.session.add(new_garbageInEvent)
            db.session.commit()

    organizador = UserInEvent(userID=current_user.id,eventID=new_event.id,status="Confirmado",creator=True)
    db.session.add(organizador)

    db.session.commit()

    return make_response(jsonify({'data': Event.serialize(new_event), 'message': '200 OK - Event Created'}), 200)


# Get All Events by User
@event_routes_blueprint.route('/events', methods=['GET'])
def get_events():
    #todo order by date
    events = db.session.query(Event).all()
    output = []
    for event in events:
        event_data = {}
        event_data['id'] = event.id
        event_data['name'] = event.name
        event_data['latitude'] = event.latitude
        event_data['longitude'] = event.longitude
        event_data['status'] = event.status
        event_data['duration'] = event.duration
        event_data['startDate'] = event.startDate
        event_data['description'] = event.description
        event_data['accessibility'] = event.accessibility
        event_data['restrictions'] = event.restrictions
        event_data['quantity'] = event.quantity
        event_data['observations'] = event.observations
        event_data['garbageSpots'] = []
        for garbageSpot in  db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id):
            garbageSpotSer = GarbageSpotInEvent.serialize(garbageSpot)
            event_data['garbageSpots'].append(garbageSpotSer)
        output.append(event_data)
        event_data['garbageType'] = []


    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Event Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)

# Get Event by ID
@event_routes_blueprint.route('/events/<event_id>', methods=['GET'])
def get_event_id(event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
            return make_response(
                jsonify({'message': '404 NOT OK - Event doesnt exist!'}),404)


    return make_response(jsonify({'data': Event.serialize(event), 'message': '200 OK - Event Retrieved'}), 200)




# Update Event by Event Organizer (User)
@event_routes_blueprint.route('/events/<event_id>', methods=['PUT'])
@token_required
def update_event(current_user, event_id):
    userInEvent = db.session.query(UserInEvent).filter_by(userID=current_user.id).all()
    event = db.session.query(UserInEvent).filter_by(id=event_id).first()

    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)

    if not userInEvent.includes(event) :
        return make_response(jsonify({'message': '403 NOT OK - You Can\'t Update This Event'}), 403)


    event_data = request.get_json()
    event.duration = event_data['duration']
    event.startDate = event_data['startDate']
    event.description = event_data['description']
    event.accessibility = event_data['accessibility']
    event.restrictions = event_data['restrictions']
    event.garbageType = event_data['garbageType']
    event.quantity = event_data['quantity']
    event.observations = event_data['observations']

    db.session.commit()


    return make_response(jsonify({'message': '200 OK - Event Updated'}), 200)



# Region Event Operations Regarding Garbage Spots

# Add Garbage Spot to Event by User
@event_routes_blueprint.route('/events/<event_id>/addGarbageSpot', methods=['POST'])
@token_required
def add_garbageSpot_to_event(current_user,event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)

    data = request.get_json()
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=data["garbageSpotID"]).first()

    if not garbageSpot:
        return make_response(jsonify({'message': '404 NOT OK - No Garbage Spot Found'}), 404)


    signUp = GarbageSpotInEvent(garbageSpotID=data['garbageSpotID'], eventID=event_id)

    db.session.add(signUp)
    db.session.commit()

    return make_response(jsonify({'message': '200 OK - Garbage Spot Added To Event'}), 200)

# Get Garbage Spots for Event
@event_routes_blueprint.route('/events/<event_id>/garbageSpots', methods=['GET'])
@token_required
def get_garbageSpots_no_event(current_user,event_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(eventID=event_id)
    result = []
    for garbageSpot in garbageSpotsEvento:
        garbageSpot_data = {}
        garbageSpot_data['id'] = garbageSpot.id
        garbageSpot_data['eventID'] = event_id
        garbageSpot_data['garbageSpotID'] = garbageSpot.garbageSpotID
        result.append(garbageSpot_data)

    return make_response(jsonify({'data': result,'message': '200 OK - All Garbage Spots Retrieved From Event'}), 200)


# Get Events for Garbage Spot
@event_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/events', methods=['GET'])
@token_required
def get_events_na_garbageSpot(current_user, garbageSpot_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot_id)
    result = []
    for event_data in garbageSpotsEvento:
        event_data = {}
        event_data['id'] = garbageSpot.id
        event_data['eventID'] = event_id
        event_data['garbageSpotID'] = garbageSpot.garbageSpotID
        result.append(event_data)

    return make_response(jsonify({'data': result,'message': '200 OK - All Events Retrieved From Garbage Spot'}), 200)


# Get Garbage Spots created by Logged User
@event_routes_blueprint.route('/events/mine', methods=['GET'])
@token_required
def get_my_events(current_user):

    myEvents = db.session.query(UserInEvent).filter_by(userID=current_user.id).all()
    print(myEvents)

    output = []

    for user_event in myEvents:
        event_data = {}
        event_data['id'] = user_event.id
        event_data['userID'] = user_event.userID
        event_data['event'] = {}
        for event in db.session.query(Event).filter_by(id=user_event.eventID):
            eventSer = Event.serialize(event)
            event_data['event'] = eventSer


        event_data['status'] = user_event.status

        output.append(event_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Events Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)


# Add user to Event by User
@event_routes_blueprint.route('/events/<event_id>/signUpEvent', methods=['POST'])
@token_required
def add_user_to_event(current_user,event_id):
   # print("Event - "+event_id)
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)


    user_event = db.session.query(UserInEvent).filter_by(userID=current_user.id).first()
    if user_event:
        if user_event.eventID == event_id:
            return make_response(jsonify({'message': '405 NOT OK - Already signed up to Event! Update your status instead!'}), 409)

    signUp = UserInEvent(userID=current_user.id, eventID=event_id,status="Inscrito",creator = False)

    db.session.add(signUp)
    db.session.commit()

    return make_response(jsonify({'message': '200 OK - User Sign Up to Event'}), 200)

@event_routes_blueprint.route('/events/<event_id>/signUpUpdateStatusEvent/<user_event_id>', methods=['Patch'])
@token_required
def update_status_user_to_event(current_user,event_id,user_event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)

    user_event = db.session.query(UserInEvent).filter_by(id=user_event_id).first()
    if not user_event:
        return make_response(jsonify({'message': '404 NOT OK - User In Event Not Found! '}), 404)

    data = request.get_json()
    #print(data)
    user_event.status = data

    db.session.commit()

    return make_response(jsonify({'message': '200 OK - User Sign Up to Event Status Updated'}), 200)
# endregion