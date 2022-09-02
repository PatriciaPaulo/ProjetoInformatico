from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
<<<<<<< HEAD
from models import User, Activity, Event, db, GarbageSpot, GarbageSpotInEvent, UnitType
from utils import token_required,admin_required,guest
=======
from models import User, Activity, Event, db, GarbageSpot, GarbageSpotInEvent
from utils import token_required, admin_required, guest
>>>>>>> origin/main
import jwt
from datetime import datetime

garbagespot_routes_blueprint = Blueprint('garbagespot_routes', __name__, )
api = Api(garbagespot_routes_blueprint)


# Create Garbage Spot
@garbagespot_routes_blueprint.route('/garbageSpots', methods=['POST'])
@guest
def create_garbage_spot(current_user):
    try:
        data = request.get_json()
        # Checks if garbageSpot with same coordinates exists
        garbageSpot = db.session.query(GarbageSpot).filter_by(latitude=data['latitude']).first()
        if garbageSpot:
            if garbageSpot.longitude == float(data['longitude']):
                return make_response(
                    jsonify({'message': '409 NOT OK - GarbageSpot already exists! Update it instead!'}), 409)

        new_garbageSpot = GarbageSpot(name=data['name'], latitude=data['latitude'],
                                      longitude=data['longitude'], creator=current_user.id,
                                      status=data['status'], approved=data['approved'], createdDate=datetime.utcnow())

        db.session.add(new_garbageSpot)
        db.session.commit()

        return make_response(
            jsonify({'data': GarbageSpot.serialize(new_garbageSpot), 'message': '200 OK - Garbage Spot Created'}), 200)

    except:
        return make_response(jsonify({'message': '400 NOT OK - Unknown error!'}), 400)


# Get All Garbage Spots
@garbagespot_routes_blueprint.route('/garbageSpots', methods=['GET'])
@guest
def get_all_garbageSpots(current_user):
    garbageSpots = db.session.query(GarbageSpot).all()

    output = []
    for garbageSpot in garbageSpots:
        garbageSpot_data = {}
        if garbageSpot.approved is True or garbageSpot.creator is current_user.id or current_user.admin:
            garbageSpot_data['id'] = garbageSpot.id
            garbageSpot_data['name'] = garbageSpot.name
            garbageSpot_data['latitude'] = garbageSpot.latitude
            garbageSpot_data['longitude'] = garbageSpot.longitude
            garbageSpot_data['creator'] = garbageSpot.creator
            garbageSpot_data['status'] = garbageSpot.status
            garbageSpot_data['approved'] = garbageSpot.approved
            garbageSpot_data['createdDate'] = garbageSpot.createdDate
            garbageSpot_data['events'] = []
            for ev in db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot.id):
                evSer = GarbageSpotInEvent.serialize(ev)
                garbageSpot_data['events'].append(evSer)

            output.append(garbageSpot_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Garbage Spot Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Garbage Spot Retrieved'}), 200)


# Get Garbage Spot by ID
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>', methods=['GET'])
@guest
def get_garbageSpot(current_user, garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()
    if not garbageSpot:
        return make_response(
            jsonify({'message': '404 NOT OK - Garbage Spot doesnt exist!'}), 404)

    if not garbageSpot.approved and garbageSpot.creator is not current_user.id:
        return make_response(
            jsonify({'message': '403 NOT OK - Garbage Spot is no approved or belong to you!'}), 403)

    output = GarbageSpot.serialize(garbageSpot)
    output["events"] = []
    for ev in db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot.id):
        evSer = GarbageSpotInEvent.serialize(ev)
        output["events"].append(evSer)

    return make_response(
        jsonify({'data': output, 'message': '200 OK - All Garbage Spot Retrieved'}), 200)


# Update Garbage Spot by User
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>', methods=['PUT'])
@token_required
def update_garbageSpot(current_user, garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id, creator=current_user.id).first()
    if not garbageSpot:
        return make_response(jsonify({'message': '404 NOT OK - No Garbage Spot Found'}), 404)

    garbageSpot_data = request.get_json()
    garbageSpot.status = garbageSpot_data['status']

    db.session.commit()
    return make_response(jsonify({'message': '200 OK - Garbage Spot Updated'}), 200)


# Approve Garbage Spot by Admin
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/approve', methods=['PATCH'])
@admin_required
def approve_garbageSpot(current_user, garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()
    if not garbageSpot:
        return make_response(jsonify({'message': '404 NOT OK - No Garbage Spot Found'}), 404)

    garbageSpot_data = request.get_json()
    garbageSpot.approved = garbageSpot_data['approved']
    print(garbageSpot_data['approved'])

    """
    if garbageSpot_data['approved'] == 'false':
        garbageSpot.approved = False
    elif garbageSpot_data['approved'] == 'true':
        garbageSpot.approved = True
    """

    db.session.commit()
    print(garbageSpot.approved)
    return make_response(jsonify({'message': '200 OK - Garbage Spot Approved'}), 200)


# Update Garbage Spot State by any User
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/updateGarbageSpotStatus', methods=['PATCH'])
@token_required
def update_status_garbageSpot(current_user, garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()
    if not garbageSpot:
        return make_response(jsonify({'message': '404 NOT OK - No Garbage Spot Found'}), 404)
    garbageSpot_data = request.get_json()

    garbageSpot.status = garbageSpot_data

    db.session.commit()
    return make_response(jsonify({'message': '200 OK - Garbage Spot Status Updated'}), 200)


# Get Garbage Spots created by Logged User
@garbagespot_routes_blueprint.route('/garbageSpots/mine', methods=['GET'])
@token_required
def get_my_garbageSpot(current_user):
    garbageSpots = db.session.query(GarbageSpot).filter_by(creator=current_user.id).first().all()
    output = []
    for garbageSpot in garbageSpots:
        garbageSpot_data = {}
        garbageSpot_data['id'] = garbageSpot.id
        garbageSpot_data['name'] = garbageSpot.name
        garbageSpot_data['latitude'] = garbageSpot.latitude
        garbageSpot_data['longitude'] = garbageSpot.longitude
        garbageSpot_data['creator'] = garbageSpot.creator
        garbageSpot_data['status'] = garbageSpot.status
        garbageSpot_data['approved'] = garbageSpot.approved
        garbageSpot_data['foto'] = garbageSpot.foto
        garbageSpot_data['events'] = []
        for ev in db.session.query(GarbageSpotInEvent).filter_by(garbageSpotID=garbageSpot.id):
            evSer = GarbageSpotInEvent.serialize(ev)
            garbageSpot_data['events'].append(evSer)
        output.append(garbageSpot_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Garbage Spot Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Garbage Spot Retrieved'}), 200)


<<<<<<< HEAD
# Get Garbage UnitTypes
@garbagespot_routes_blueprint.route('/unitTypes', methods=['GET'])
def get_unit_types():

    unit_types = db.session.query(UnitType).all()

    output = []
    for ut in unit_types:
        ut_data = {}

        ut_data['id'] = ut.id
        ut_data['name'] = ut.name

        output.append(ut_data)

    if len(output) == 0:
        return make_response(jsonify({'data':[],'message':'404 NOT OK - No Units Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Units Retrieved'}), 200)
=======
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>', methods=['DELETE'])
@admin_required
def delete_garbage_spot(current_user, garbageSpot_id):
    # Checks if user exists
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()
    if not garbageSpot:
        return make_response(jsonify({'message': '404 NOT OK - No Garbage Spot Found'}), 404)

    db.session.delete(garbageSpot)
    db.session.commit()
    return make_response(jsonify({'message': '200 OK - Garbage Spot Deleted'}), 200)
>>>>>>> origin/main
