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
@garbagespot_routes_blueprint.route('/lixeiras', methods=['POST'])
@guest
def create_lixeira(current_user):
    data = request.get_json()
    # Checks if lixeira with same coordinates exists
    lix = db.session.query(GarbageSpot).filter_by(latitude=data['latitude']).first()
    if lix:
          if lix.longitude == float(data['longitude']) :
            return make_response(jsonify({'message': 'GarbageSpot already exists! Update it instead!','status': 409}))


    new_lixeira = GarbageSpot(nome=data['name'], latitude=data['latitude'], longitude=data['longitude'], criador=current_user.id,
                              estado=data['status'], aprovado=data['approved'], foto=data['foto'])
    db.session.add(new_lixeira)
    db.session.commit()
    return make_response(jsonify({'message': 'local lixo criado','status':  200}))


# Get All Garbage Spots
@garbagespot_routes_blueprint.route('/lixeiras', methods=['GET'])
def get_all_lixeiras():
    lixeiras = db.session.query(GarbageSpot).all()
    output = []
    for lixeira in lixeiras:
        lixeira_data = {}
        lixeira_data['id'] = lixeira.id
        lixeira_data['name'] = lixeira.name
        lixeira_data['latitude'] = lixeira.latitude
        lixeira_data['longitude'] = lixeira.longitude
        lixeira_data['creator'] = lixeira.creator
        lixeira_data['status'] = lixeira.status
        lixeira_data['approved'] = lixeira.approved
        lixeira_data['foto'] = lixeira.foto
        lixeira_data['eventos'] = []
        for ev in db.session.query(GarbageSpotInEvent).filter_by(lixeiraID=lixeira.id):
            evSer = GarbageSpotInEvent.serialize(ev)
            lixeira_data['eventos'].append(evSer)
        output.append(lixeira_data)
    if len(output) == 0:
        return make_response(jsonify({'date':[],'staus':404,'message':'no locais lixo'}))

    return make_response(jsonify({'date': output,'status':200,'message':'locais lixo encontrados'}))


# Get Garbage Spot by ID
@garbagespot_routes_blueprint.route('/lixeiras/<lixeira_id>', methods=['GET'])
def get_lixeira(current_user,lixeira_id):
    lixeira = db.session.query(GarbageSpot).filter_by(id=lixeira_id).first()

    return make_response(jsonify({'date': GarbageSpot.serialize(lixeira), 'status':200}))


# Update Garbage Spot by User
@garbagespot_routes_blueprint.route('/lixeiras/<lixeira_id>', methods=['PUT'])
@token_required
def update_lixeira(current_user, lixeira_id):
    lixeira = db.session.query(GarbageSpot).filter_by(id=lixeira_id, criador=current_user.id).first()
    if not evento:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)

    lixeira_data = request.get_json()
    lixeira.status = lixeira_data['status']
    evento.foto = evento_data['foto']

    db.session.commit()
    return make_response(jsonify({'message': 'lixeira atualizada'}), 200)



# Approve Garbage Spot by Admin
@garbagespot_routes_blueprint.route('/lixeiras/<lixeira_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_lixeira(current_user,lixeira_id):
    lixeira = db.session.query(GarbageSpot).filter_by(id=lixeira_id).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)
    lixeira_data = request.get_json()

    lixeira.approved = lixeira_data['approved']
    if lixeira_data['approved'] == 'false':
        lixeira.approved = False
    elif lixeira_data['approved'] == 'true':
        lixeira.approved = True

    db.session.commit()
    return make_response(jsonify({'message': 'lixeira atualizada'}), 200)


# Update Garbage Spot State by any User
@garbagespot_routes_blueprint.route('/lixeiras/<lixeira_id>/mudarEstadoLixeira', methods=['PATCH'])
@token_required
def mudar_estado_lixeira(current_user,lixeira_id):
    lixeira = db.session.query(GarbageSpot).filter_by(id=lixeira_id).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)
    lixeira_data = request.get_json()

    lixeira.status = lixeira_data['status']
    lixeira_data['status'] = lixeira.status

    db.session.commit()
    return make_response(jsonify({'message': 'status do local de lixo atualizado','status':200}))


# Get Garbage Spots created by Logged User
@garbagespot_routes_blueprint.route('/lixeiras/mine', methods=['GET'])
@token_required
def get_my_lixeira(current_user):
    lixeiras = db.session.query(GarbageSpot).filter_by(criador=current_user.id).first().all()
    output = []
    for lixeira in lixeiras:
        lixeira_data = {}
        lixeira_data['id'] = lixeira.id
        lixeira_data['name'] = lixeira.name
        lixeira_data['latitude'] = lixeira.latitude
        lixeira_data['longitude'] = lixeira.longitude
        lixeira_data['creator'] = lixeira.creator
        lixeira_data['status'] = lixeira.status
        lixeira_data['approved'] = lixeira.approved
        lixeira_data['foto'] = lixeira.foto
        lixeira_data['eventos'] = []
        for ev in db.session.query(GarbageSpotInEvent).filter_by(lixeiraID=lixeira.id):
            evSer = GarbageSpotInEvent.serialize(ev)
            lixeira_data['eventos'].append(evSer)
        output.append(lixeira_data)
    if len(output) == 0:
        return make_response(jsonify({'date': [], 'staus': 404, 'message': 'no locais lixo'}))

    return make_response(jsonify({'date': output, 'status': 200, 'message': 'locais lixo encontrados'}))
