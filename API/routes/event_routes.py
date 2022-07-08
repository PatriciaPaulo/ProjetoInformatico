from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot,GarbageSpotInEvent
from utils import token_required,admin_required,guest
import jwt
import datetime

event_routes_blueprint = Blueprint('event_routes', __name__, )
api = Api(event_routes_blueprint)

# Create Event by User
@event_routes_blueprint.route('/events', methods=['POST'])
@token_required
def create_event(current_user):
    data = request.get_json()

    new_event = Event(name=data['name'], latitude=data['latitude'], longitude=data['longitude'], organizer=current_user.id,
                       status=data['status'], duration=data['duration'], description=data['description'],
                       accessibility=data['accessibility'], restrictions=data['restrictions'], garbageType=data['garbageType'],
                       quantity=data['quantity'], foto=data['foto'], comments=data['observations'], startDate=data['startDate'],
                       )
    db.session.add(new_event)
    db.session.commit()

    return make_response(jsonify({'data': GarbageSpot.serialize(new_event), 'message': '200 OK - Event Created'}), 200)


# Get All Events by User
@event_routes_blueprint.route('/events', methods=['GET'])
@token_required
def get_events(current_user):
    #todo order by date
    events = db.session.query(Event).all()
    output = []
    for event in events:
        event_data = {}
        event_data['id'] = event.id
        event_data['name'] = event.name
        event_data['organizer'] = event.organizer
        event_data['latitude'] = event.name
        event_data['longitude'] = event.name
        event_data['status'] = event.status
        event_data['duration'] = event.duration
        event_data['startDate'] = event.startDate
        event_data['description'] = event.description
        event_data['accessibility'] = event.accessibility
        event_data['restrictions'] = event.restrictions
        event_data['garbageType'] = event.garbageType
        event_data['quantity'] = event.quantity
        event_data['foto'] = event.foto
        event_data['observations'] = event.observations
        event_data['garbageSpots'] = []
        for garbageSpot in  db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id):
            garbageSpotSer = GarbageSpotInEvent.serialize(garbageSpot)
            event_data['garbageSpots'].append(garbageSpotSer)
        output.append(event_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Event Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)




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
def get_events_na_garbageSpotiera(current_user, garbageSpot_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot_id)
    result = []
    for event_data in garbageSpotsEvento:
        event_data = {}
        event_data['id'] = garbageSpot.id
        event_data['eventID'] = event_id
        event_data['garbageSpotID'] = garbageSpot.garbageSpotID
        result.append(event_data)

    return make_response(jsonify({'data': result,'message': '200 OK - All Events Retrieved From Garbage Spot'}), 200)

# endregion