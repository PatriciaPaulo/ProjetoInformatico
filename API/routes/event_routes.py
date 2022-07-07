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
@event_routes_blueprint.route('/eventos', methods=['POST'])
@token_required
def create_evento(current_user):
    data = request.get_json()

    new_evento = Event(nome=data['name'], latitude=data['latitude'], longitude=data['longitude'], organizador=current_user.id,
                       estado=data['status'], duracao=data['duration'], descricao=data['description'],
                       acessibilidade=data['accessibility'], restricoes=data['restrictions'], tipoLixo=data['garbageType'],
                       volume=data['volume'], foto=data['foto'], observacoes=data['observations'], dataInicio=data['startDate'],
                       )
    db.session.add(new_evento)
    db.session.commit()
    return make_response(jsonify({'message': 'new evento created'}), 200)



# Get All Events by User
@event_routes_blueprint.route('/eventos', methods=['GET'])
@token_required
def get_eventos(current_user):
    #todo order by date
    eventos = db.session.query(Event).all()
    output = []
    for evento in eventos:
        evento_data = {}
        evento_data['id'] = evento.id
        evento_data['name'] = evento.name
        evento_data['organizer'] = evento.organizer
        evento_data['latitude'] = evento.name
        evento_data['longitude'] = evento.name
        evento_data['status'] = evento.status
        evento_data['duration'] = evento.duration
        evento_data['startDate'] = evento.startDate
        evento_data['description'] = evento.description
        evento_data['accessibility'] = evento.accessibility
        evento_data['restrictions'] = evento.restrictions
        evento_data['garbageType'] = evento.garbageType
        evento_data['volume'] = evento.volume
        evento_data['foto'] = evento.foto
        evento_data['observations'] = evento.observations
        evento_data['lixeiras'] = []
        for lix in  db.session.query(GarbageSpotInEvent).filter_by(eventoID=evento.id):
            lixSer = GarbageSpotInEvent.serialize(lix)
            evento_data['lixeiras'].append(lixSer)
        output.append(evento_data)




    return make_response(jsonify({'date': output}), 200)


# Update Event by Event Organizer (User)
@event_routes_blueprint.route('/eventos/<evento_id>', methods=['PUT'])
@token_required
def update_evento(current_user, evento_id):
    evento = db.session.query(Event).filter_by(id=evento_id, organizador=current_user.username).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 400)

    evento_data = request.get_json()
    evento.duration = evento_data['duration']
    evento.startDate = evento_data['startDate']
    evento.description = evento_data['description']
    evento.accessibility = evento_data['accessibility']
    evento.restrictions = evento_data['restrictions']
    evento.garbageType = evento_data['garbageType']
    evento.volume = evento_data['volume']
    evento.foto = evento_data['foto']
    evento.observations = evento_data['observations']

    db.session.commit()
    return make_response(jsonify({'message': 'evento atualizada'}), 200)



# Approve Event by Admin
@event_routes_blueprint.route('/eventos/<evento_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_evento(evento_id):
    evento = db.session.query(Event).filter_by(id=evento_id).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 400)

    evento_data = request.get_json()

    evento.status = evento_data['status']

    db.session.commit()
    return make_response(jsonify({'message': 'evento atualizada'}), 200)

# Region Event Operations Regarding Garbage Spots

# Add Garbage Spot to Event by User
@event_routes_blueprint.route('/eventos/<evento_id>/addLixeira', methods=['POST'])
@token_required
def add_lixeira_to_event(current_user,evento_id):
    evento = db.session.query(Event).filter_by(id=evento_id).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 404)

    data = request.get_json()
    lixeira = db.session.query(GarbageSpot).filter_by(id=data["garbageSpotID"]).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)


    inscricao = GarbageSpotInEvent(lixeiraID=data['garbageSpotID'], eventoID=evento_id)

    db.session.add(inscricao)
    db.session.commit()
    return make_response(jsonify({'message': 'lixeira adicionada ao evento'}), 200)

# Get Garbage Spots for Event
@event_routes_blueprint.route('/eventos/<evento_id>/lixeiras', methods=['GET'])
@token_required
def get_lixeiras_no_evento(current_user,evento_id):
    lixeirasEvento = db.session.query(GarbageSpotInEvent).filter_by(eventoID=evento_id)
    result = []
    for lix in lixeirasEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventID'] = evento_id
        lix_data['garbageSpotID'] = lix.garbageSpotID
        result.append(lix_data)

    return make_response(jsonify({'date': result}), 200)


# Get Events for Garbage Spot
@event_routes_blueprint.route('/lixeiras/<lixeira_id>/eventos', methods=['GET'])
@token_required
def get_eventos_na_lixiera(current_user, lixeira_id):
    lixeirasEvento = db.session.query(GarbageSpotInEvent).filter_by(lixeiraID=lixeira_id)
    result = []
    for lix in lixeirasEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventID'] = evento_id
        lix_data['garbageSpotID'] = lix.garbageSpotID
        result.append(lix_data)

    return make_response(jsonify({'date': result}), 200)

# endregion