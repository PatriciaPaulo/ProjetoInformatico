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
    return make_response(jsonify({'message': 'new event created'}), 200)



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
        for lix in  db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id):
            lixSer = GarbageSpotInEvent.serialize(lix)
            event_data['garbageSpots'].append(lixSer)
        output.append(event_data)




    return make_response(jsonify({'date': output}), 200)


# Update Event by Event Organizer (User)
@event_routes_blueprint.route('/events/<event_id>', methods=['PUT'])
@token_required
def update_event(current_user, event_id):
    event = db.session.query(Event).filter_by(id=event_id, organizer=current_user.username).first()
    if not event:
        return make_response(jsonify({'message': 'event does not exist'}), 400)

    event_data = request.get_json()
    event.duration = event_data['duration']
    event.startDate = event_data['startDate']
    event.description = event_data['description']
    event.accessibility = event_data['accessibility']
    event.restrictions = event_data['restrictions']
    event.garbageType = event_data['garbageType']
    event.quantity = event_data['quantity']
    event.foto = event_data['foto']
    event.observations = event_data['observations']

    db.session.commit()
    return make_response(jsonify({'message': 'event atualizada'}), 200)



# Approve Event by Admin
@event_routes_blueprint.route('/events/<event_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_event(event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': 'event does not exist'}), 400)

    event_data = request.get_json()

    event.status = event_data['status']

    db.session.commit()
    return make_response(jsonify({'message': 'event atualizada'}), 200)

# Region Event Operations Regarding Garbage Spots

# Add Garbage Spot to Event by User
@event_routes_blueprint.route('/events/<event_id>/addLixeira', methods=['POST'])
@token_required
def add_garbageSpot_to_event(current_user,event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': 'event does not exist'}), 404)

    data = request.get_json()
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=data["garbageSpotID"]).first()
    if not garbageSpot:
        return make_response(jsonify({'message': 'garbageSpot does not exist'}), 404)


    inscricao = GarbageSpotInEvent(garbageSpotID=data['garbageSpotID'], eventID=event_id)

    db.session.add(inscricao)
    db.session.commit()
    return make_response(jsonify({'message': 'garbageSpot adicionada ao event'}), 200)

# Get Garbage Spots for Event
@event_routes_blueprint.route('/events/<event_id>/garbageSpots', methods=['GET'])
@token_required
def get_garbageSpots_no_event(current_user,event_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(eventID=event_id)
    result = []
    for lix in garbageSpotsEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventID'] = event_id
        lix_data['garbageSpotID'] = lix.garbageSpotID
        result.append(lix_data)

    return make_response(jsonify({'date': result}), 200)


# Get Events for Garbage Spot
@event_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/events', methods=['GET'])
@token_required
def get_events_na_lixiera(current_user, garbageSpot_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot_id)
    result = []
    for lix in garbageSpotsEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventID'] = event_id
        lix_data['garbageSpotID'] = lix.garbageSpotID
        result.append(lix_data)

    return make_response(jsonify({'date': result}), 200)

# endregion