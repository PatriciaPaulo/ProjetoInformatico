from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot,GarbageSpotInEvent
from utils import token_required,admin_required,guest
import jwt
import datetime

garbagespot_routes_blueprint = Blueprint('garbagespot_routes', __name__, )
api = Api(garbagespot_routes_blueprint)

# Create Garbage Spot by Guest
@garbagespot_routes_blueprint.route('/garbageSpots', methods=['POST'])
@guest
def create_garbageSpot(current_user):
    data = request.get_json()
    # Checks if garbageSpot with same coordinates exists
    lix = db.session.query(GarbageSpot).filter_by(latitude=data['latitude']).first()
    if lix:
          if lix.longitude == float(data['longitude']) :
            return make_response(jsonify({'message': 'GarbageSpot already exists! Update it instead!','status': 409}))


    new_garbageSpot = GarbageSpot(name=data['name'], latitude=data['latitude'], longitude=data['longitude'], creator=current_user.id,
                              status=data['status'], approved=data['approved'], foto=data['foto'])
    db.session.add(new_garbageSpot)
    db.session.commit()
    return make_response(jsonify({'message': 'local garbage criado','status':  200}))


# Get All Garbage Spots
@garbagespot_routes_blueprint.route('/garbageSpots', methods=['GET'])
def get_all_garbageSpots():
    garbageSpots = db.session.query(GarbageSpot).all()
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
        return make_response(jsonify({'date':[],'staus':404,'message':'no locais garbage'}))

    return make_response(jsonify({'date': output,'status':200,'message':'locais garbage encontrados'}))


# Get Garbage Spot by ID
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>', methods=['GET'])
def get_garbageSpot(current_user,garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()

    return make_response(jsonify({'date': GarbageSpot.serialize(garbageSpot), 'status':200}))


# Update Garbage Spot by User
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>', methods=['PUT'])
@token_required
def update_garbageSpot(current_user, garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id, creator=current_user.id).first()
    if not event:
        return make_response(jsonify({'message': 'garbageSpot does not exist'}), 404)

    garbageSpot_data = request.get_json()
    garbageSpot.status = garbageSpot_data['status']
    event.foto = event_data['foto']

    db.session.commit()
    return make_response(jsonify({'message': 'garbageSpot atualizada'}), 200)



# Approve Garbage Spot by Admin
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_garbageSpot(current_user,garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()
    if not garbageSpot:
        return make_response(jsonify({'message': 'garbageSpot does not exist'}), 404)
    garbageSpot_data = request.get_json()

    garbageSpot.approved = garbageSpot_data['approved']
    if garbageSpot_data['approved'] == 'false':
        garbageSpot.approved = False
    elif garbageSpot_data['approved'] == 'true':
        garbageSpot.approved = True

    db.session.commit()
    return make_response(jsonify({'message': 'garbageSpot atualizada'}), 200)


# Update Garbage Spot State by any User
@garbagespot_routes_blueprint.route('/garbageSpots/<garbageSpot_id>/mudarEstadoLixeira', methods=['PATCH'])
@token_required
def mudar_status_garbageSpot(current_user,garbageSpot_id):
    garbageSpot = db.session.query(GarbageSpot).filter_by(id=garbageSpot_id).first()
    if not garbageSpot:
        return make_response(jsonify({'message': 'garbageSpot does not exist'}), 404)
    garbageSpot_data = request.get_json()

    garbageSpot.status = garbageSpot_data['status']
    garbageSpot_data['status'] = garbageSpot.status

    db.session.commit()
    return make_response(jsonify({'message': 'status do local de garbage atualizado','status':200}))


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
        return make_response(jsonify({'date': [], 'staus': 404, 'message': 'no locais garbage'}))

    return make_response(jsonify({'date': output, 'status': 200, 'message': 'locais garbage encontrados'}))
