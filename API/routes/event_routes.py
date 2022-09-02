from flask import jsonify, make_response, request
from flask_restful import Api
from flask import Blueprint
from sqlalchemy import desc

from models import Event, db, GarbageSpot, GarbageSpotInEvent, UserInEvent, GarbageInEvent, Garbage, Equipment, \
    EquipmentInEvent, User
from utils import token_required
from datetime import datetime

from websockets_server import send_notification_event_status

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
            return make_response(
                jsonify({'message': '409 NOT OK - Event already exists! Try to sign up as organizer instead!'}),
                409)

    start_date_time_obj = datetime.strptime(data['event']['startDate'], '%Y-%m-%dT%H:%M:%SZ')

    print(start_date_time_obj)
    print(datetime.utcnow())
    if start_date_time_obj < datetime.utcnow():
        return make_response(jsonify({'message': '404 NOT OK - Event date invalid!'}), 404)

    new_event = Event(name=data['event']['name'], latitude=data['event']['latitude'],
                      longitude=data['event']['longitude'],
                      status=data['event']['status'], duration=data['event']['duration'],
                      description=data['event']['description'],
                      accessibility=data['event']['accessibility'], restrictions=data['event']['restrictions'],
                      quantity=data['event']['quantity'], observations=data['event']['observations'],
                      startDate=start_date_time_obj, createdDate=datetime.utcnow()
                      )

    db.session.add(new_event)
    db.session.flush()

    for garbageTypeID in data['garbageTypeList']:
        garbageExists = db.session.query(Garbage).filter_by(id=garbageTypeID).first()
        if garbageExists:
            new_garbageInEvent = GarbageInEvent(eventID=new_event.id, garbageID=garbageTypeID)
            db.session.add(new_garbageInEvent)

    organizador = UserInEvent(userID=current_user.id, eventID=new_event.id, status="Organizer", creator=True, enteringDate=datetime.utcnow())
    db.session.add(organizador)

    if len(data['garbageSpotList']) > 0:
        for garbageSpotID in data['garbageSpotList']:
            garbageSpotExists = db.session.query(GarbageSpot).filter_by(id=garbageSpotID).first()
            if garbageSpotExists:
                new_garbageSpotInEvent = GarbageSpotInEvent(eventID=new_event.id, garbageSpotID=garbageSpotID)
                db.session.add(new_garbageSpotInEvent)

    if len(data['equipmentList']) > 0:
        for equipment in data['equipmentList']:
            print(equipment)
            equipmentExists = db.session.query(Equipment).filter_by(id=equipment['equipmentID']).first()
            if equipmentExists:
                new_equipmentInEvent = EquipmentInEvent(eventID=new_event.id, equipmentID=equipment['equipmentID'],
                                                        observations=equipment['observations'],
                                                        isProvided=equipment['isProvided'])
                db.session.add(new_equipmentInEvent)

    db.session.commit()

    return make_response(jsonify({'data': Event.serialize(new_event), 'message': '200 OK - Event Created'}), 200)


# Get All Events by User
@event_routes_blueprint.route('/events', methods=['GET'])
def get_events():
    # todo order by date
    events = db.session.query(Event).order_by(desc(Event.createdDate)).all()
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
        event_data['createdDate'] = event.createdDate
        event_data['garbageSpots'] = []
        for garbageSpot in db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id):
            garbageSpotSer = GarbageSpotInEvent.serialize(garbageSpot)
            event_data['garbageSpots'].append(garbageSpotSer)

        event_data['garbageTypes'] = []
        for garbageType in db.session.query(GarbageInEvent).filter_by(eventID=event.id):
            garbageTypeSer = GarbageInEvent.serialize(garbageType)
            event_data['garbageTypes'].append(garbageTypeSer)

        event_data['equipments'] = []
        for equipment in db.session.query(EquipmentInEvent).filter_by(eventID=event.id):
            equipmentSer = EquipmentInEvent.serialize(equipment)
            event_data['equipments'].append(equipmentSer)
        output.append(event_data)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)


# Get Event by ID
@event_routes_blueprint.route('/events/<event_id>', methods=['GET'])
def get_event_id(event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(
            jsonify({'message': '404 NOT OK - Event doesnt exist!'}), 404)

    event_data = {'id': event.id, 'name': event.name, 'latitude': event.latitude, 'longitude': event.longitude,
                  'status': event.status, 'duration': event.duration, 'startDate': event.startDate,
                  'description': event.description, 'accessibility': event.accessibility,
                  'restrictions': event.restrictions, 'quantity': event.quantity, 'observations': event.observations,
                  'createdDate': event.createdDate, 'garbageSpots': [], 'garbageTypes': [], 'equipments': []}

    for garbageSpot in db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id):
        garbageSpotSer = GarbageSpotInEvent.serialize(garbageSpot)
        event_data['garbageSpots'].append(garbageSpotSer)

    for garbageType in db.session.query(GarbageInEvent).filter_by(eventID=event.id):
        garbageTypeSer = GarbageInEvent.serialize(garbageType)
        event_data['garbageTypes'].append(garbageTypeSer)

    for equipment in db.session.query(EquipmentInEvent).filter_by(eventID=event.id):
        equipmentSer = EquipmentInEvent.serialize(equipment)
        event_data['equipments'].append(equipmentSer)

    return make_response(jsonify({'data': event_data, 'message': '200 OK - Event Retrieved'}), 200)


# Get Events Creator
@event_routes_blueprint.route('/events/<event_id>/creator', methods=['GET'])
@token_required
def get_events_creator(current_user,event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(
            jsonify({'message': '404 NOT OK - Event doesnt exist!'}), 404)

    userInEvent = db.session.query(UserInEvent).filter_by(creator=True).first()
    if not userInEvent:
        return make_response(jsonify({'message': '404 NOT OK - Creator Not Found'}), 403)

    user = db.session.query(User).filter_by(id=userInEvent.userID).first()
    if not user:
        return make_response(
            jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

    return make_response(jsonify({'data': user.serialize(), 'message': '200 OK - Event Creator Retrieved'}), 200)

# Update Event by Event Organizer (User)
@event_routes_blueprint.route('/events/<event_id>', methods=['PUT'])
@token_required
def update_event(current_user, event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()

    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)

    userInEvent = db.session.query(UserInEvent).filter_by(userID=current_user.id, eventID=event_id).first()
    if userInEvent and userInEvent.status != "Organizer":
        return make_response(jsonify({'message': '403 NOT OK - You Can\'t Update This Event'}), 403)

    event_data = request.get_json()
    event.duration = event_data['event']['duration']

    start_date_time_obj = datetime.strptime(event_data['event']['startDate'], '%Y-%m-%dT%H:%M:%SZ')
    print(start_date_time_obj)
    print(datetime.utcnow())
    if start_date_time_obj < datetime.utcnow():
        return make_response(jsonify({'message': '404 NOT OK - Event date invalid!'}), 404)

    event.startDate = start_date_time_obj
    event.description = event_data['event']['description']
    event.accessibility = event_data['event']['accessibility']
    event.restrictions = event_data['event']['restrictions']
    event.quantity = event_data['event']['quantity']
    event.observations = event_data['event']['observations']

    db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id).delete()
    db.session.query(GarbageInEvent).filter_by(eventID=event.id).delete()
    db.session.query(EquipmentInEvent).filter_by(eventID=event.id).delete()
    db.session.commit()

    print(event_data['garbageTypeList'])
    print(event_data['garbageSpotList'])
    print(event_data['equipmentList'])

    for garbageTypeID in event_data['garbageTypeList']:
        garbageExists = db.session.query(Garbage).filter_by(id=garbageTypeID).first()
        if garbageExists:
            new_garbageInEvent = GarbageInEvent(eventID=event.id, garbageID=garbageTypeID)
            db.session.add(new_garbageInEvent)

    for equipment in event_data['equipmentList']:
        equipmentExists = db.session.query(Equipment).filter_by(id=equipment['equipmentID']).first()
        if equipmentExists:
            new_equipmentInEvent = EquipmentInEvent(eventID=event.id, equipmentID=equipment['equipmentID'],
                                                    observations=equipment['observations'],
                                                    isProvided=equipment['isProvided'])
            db.session.add(new_equipmentInEvent)

    if len(event_data['garbageSpotList']) > 0:
        for garbageSpotID in event_data['garbageSpotList']:
            garbageSpotExists = db.session.query(GarbageSpot).filter_by(id=garbageSpotID).first()
            if garbageSpotExists:
                new_garbageSpotInEvent = GarbageSpotInEvent(eventID=event.id, garbageSpotID=garbageSpotID)
                db.session.add(new_garbageSpotInEvent)



    db.session.commit()

    return make_response(jsonify({'message': '200 OK - Event Updated'}), 200)


# Update Event by Event Organizer (User)
@event_routes_blueprint.route('/events/<event_id>/updateStatus', methods=['PATCH'])
@token_required
def update_status_event(current_user, event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()

    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)
    userInEvent = db.session.query(UserInEvent).filter_by(userID=current_user.id, eventID=event_id).first()

    if not userInEvent and not current_user.admin:
        return make_response(jsonify({'message': '404 NOT OK - User in Event Not Found'}), 404)

    if not current_user.admin and userInEvent.status != "Organizer":
        return make_response(jsonify({'message': '403 NOT OK - You Can\'t Update This Event'}), 403)

    event_data = request.get_json()

    event.status = event_data

    db.session.commit()
    if not current_user.admin:
        send_notification_event_status(event, current_user)
    return make_response(jsonify({'message': '200 OK - Event Updated'}), 200)


# Region Event Operations Regarding Garbage Spots
"""
# Add Garbage Spot to Event by User
@event_routes_blueprint.route('/events/<event_id>/addGarbageSpot', methods=['POST'])
@token_required
def add_garbageSpots_to_event(current_user, event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)

    data = request.get_json()
    for garbageSpot in data:
        garbageSpot = db.session.query(GarbageSpot).filter_by(id=data["garbageSpotID"]).first()
    if garbageSpot:
        signUp = GarbageSpotInEvent(garbageSpotID=data['garbageSpotID'], eventID=event_id)

    db.session.add(signUp)
    db.session.commit()

    return make_response(jsonify({'message': '200 OK - Garbage Spot Added To Event'}), 200)


# Get Garbage Spots for Event
@event_routes_blueprint.route('/events/<event_id>/garbageSpots', methods=['GET'])
@token_required
def get_garbageSpots_no_event(current_user, event_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(eventID=event_id)
    result = []
    for garbageSpot in garbageSpotsEvento:
        garbageSpot_data = {}
        garbageSpot_data['id'] = garbageSpot.id
        garbageSpot_data['eventID'] = event_id
        garbageSpot_data['garbageSpotID'] = garbageSpot.garbageSpotID
        result.append(garbageSpot_data)

    return make_response(jsonify({'data': result, 'message': '200 OK - All Garbage Spots Retrieved From Event'}), 200)

"""


# Get Events for Garbage Spot
@event_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/events', methods=['GET'])
@token_required
def get_events_na_garbageSpot(current_user, garbageSpot_id):
    garbageSpotsEvento = db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot_id).all()
    result = []
    for garbageSpot in garbageSpotsEvento:
        event_data = {}
        event_data['id'] = garbageSpot.id
        event_data['eventID'] = garbageSpot.event_id
        event_data['garbageSpotID'] = garbageSpot.garbageSpotID
        result.append(event_data)

    return make_response(jsonify({'data': result, 'message': '200 OK - All Events Retrieved From Garbage Spot'}), 200)


# Get Garbage Spots created by Logged User
@event_routes_blueprint.route('/events/mine', methods=['GET'])
@token_required
def get_my_events(current_user):
    myEvents = db.session.query(UserInEvent).filter_by(userID=current_user.id).all()

    output = []

    for user_event in myEvents:
        event_data = {}
        event_data['id'] = user_event.id
        event_data['userID'] = user_event.userID

        for event in db.session.query(Event).filter_by(id=user_event.eventID):
            event_data['event'] = {'id': event.id, 'name': event.name, 'latitude': event.latitude,
                                   'longitude': event.longitude,
                                   'status': event.status, 'duration': event.duration, 'startDate': event.startDate,
                                   'description': event.description, 'accessibility': event.accessibility,
                                   'restrictions': event.restrictions, 'quantity': event.quantity,
                                   'observations': event.observations,
                                   'createdDate': event.createdDate, 'garbageSpots': [], 'garbageTypes': [],
                                   'equipments': []}

            for garbageSpot in db.session.query(GarbageSpotInEvent).filter_by(eventID=event.id):
                garbageSpotSer = GarbageSpotInEvent.serialize(garbageSpot)
                event_data['event']['garbageSpots'].append(garbageSpotSer)

            for garbageType in db.session.query(GarbageInEvent).filter_by(eventID=event.id):
                garbageTypeSer = GarbageInEvent.serialize(garbageType)
                event_data['event']['garbageTypes'].append(garbageTypeSer)

            for equipment in db.session.query(EquipmentInEvent).filter_by(eventID=event.id):
                equipmentSer = EquipmentInEvent.serialize(equipment)
                event_data['event']['equipments'].append(equipmentSer)
        event_data['status'] = user_event.status
        event_data['enteringDate'] = user_event.enteringDate
        event_data['creator'] = user_event.creator

        output.append(event_data)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)
