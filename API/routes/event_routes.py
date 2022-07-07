from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, Atividade, Evento, db, Lixeira,LixeiraEvento
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

    new_evento = Evento(nome=data['nome'], latitude=data['latitude'], longitude=data['longitude'],organizador=current_user.id,
                        estado=data['estado'], duracao=data['duracao'], descricao=data['descricao'],
                        acessibilidade=data['acessibilidade'], restricoes=data['restricoes'], tipoLixo=data['tipoLixo'],
                        volume=data['volume'], foto=data['foto'], observacoes=data['observacoes'],dataInicio=data['dataInicio'],
                        )
    db.session.add(new_evento)
    db.session.commit()
    return make_response(jsonify({'message': 'new evento created'}), 200)



# Get All Events by User
@event_routes_blueprint.route('/eventos', methods=['GET'])
@token_required
def get_eventos(current_user):
    #todo order by data
    eventos = db.session.query(Evento).all()
    output = []
    for evento in eventos:
        evento_data = {}
        evento_data['id'] = evento.id
        evento_data['nome'] = evento.nome
        evento_data['organizador'] = evento.organizador
        evento_data['latitude'] = evento.nome
        evento_data['longitude'] = evento.nome
        evento_data['estado'] = evento.estado
        evento_data['duracao'] = evento.duracao
        evento_data['dataInicio'] = evento.dataInicio
        evento_data['descricao'] = evento.descricao
        evento_data['acessibilidade'] = evento.acessibilidade
        evento_data['restricoes'] = evento.restricoes
        evento_data['tipoLixo'] = evento.tipoLixo
        evento_data['volume'] = evento.volume
        evento_data['foto'] = evento.foto
        evento_data['observacoes'] = evento.observacoes
        evento_data['lixeiras'] = []
        for lix in  db.session.query(LixeiraEvento).filter_by(eventoID=evento.id):
            lixSer = LixeiraEvento.serialize(lix)
            evento_data['lixeiras'].append(lixSer)
        output.append(evento_data)




    return make_response(jsonify({'data': output}), 200)


# Update Event by Event Organizer (User)
@event_routes_blueprint.route('/eventos/<evento_id>', methods=['PUT'])
@token_required
def update_evento(current_user, evento_id):
    evento = db.session.query(Evento).filter_by(id=evento_id, organizador=current_user.username).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 400)

    evento_data = request.get_json()
    evento.duracao = evento_data['duracao']
    evento.dataInicio = evento_data['dataInicio']
    evento.descricao = evento_data['descricao']
    evento.acessibilidade = evento_data['acessibilidade']
    evento.restricoes = evento_data['restricoes']
    evento.tipoLixo = evento_data['tipoLixo']
    evento.volume = evento_data['volume']
    evento.foto = evento_data['foto']
    evento.observacoes = evento_data['observacoes']

    db.session.commit()
    return make_response(jsonify({'message': 'evento atualizada'}), 200)



# Approve Event by Admin
@event_routes_blueprint.route('/eventos/<evento_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_evento(evento_id):
    evento = db.session.query(Evento).filter_by(id=evento_id).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 400)

    evento_data = request.get_json()

    evento.estado = evento_data['estado']

    db.session.commit()
    return make_response(jsonify({'message': 'evento atualizada'}), 200)

# Region Event Operations Regarding Garbage Spots

# Add Garbage Spot to Event by User
@event_routes_blueprint.route('/eventos/<evento_id>/addLixeira', methods=['POST'])
@token_required
def add_lixeira_to_event(current_user,evento_id):
    evento = db.session.query(Evento).filter_by(id=evento_id).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 404)

    data = request.get_json()
    lixeira = db.session.query(Lixeira).filter_by(id=data["lixeiraID"]).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)


    inscricao = LixeiraEvento(lixeiraID=data['lixeiraID'],eventoID=evento_id)

    db.session.add(inscricao)
    db.session.commit()
    return make_response(jsonify({'message': 'lixeira adicionada ao evento'}), 200)

# Get Garbage Spots for Event
@event_routes_blueprint.route('/eventos/<evento_id>/lixeiras', methods=['GET'])
@token_required
def get_lixeiras_no_evento(current_user,evento_id):
    lixeirasEvento = db.session.query(LixeiraEvento).filter_by(eventoID=evento_id)
    result = []
    for lix in lixeirasEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventoID'] = evento_id
        lix_data['lixeiraID'] = lix.lixeiraID
        result.append(lix_data)

    return make_response(jsonify({'data': result}), 200)


# Get Events for Garbage Spot
@event_routes_blueprint.route('/lixeiras/<lixeira_id>/eventos', methods=['GET'])
@token_required
def get_eventos_na_lixiera(current_user, lixeira_id):
    lixeirasEvento = db.session.query(LixeiraEvento).filter_by(lixeiraID=lixeira_id)
    result = []
    for lix in lixeirasEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventoID'] = evento_id
        lix_data['lixeiraID'] = lix.lixeiraID
        result.append(lix_data)

    return make_response(jsonify({'data': result}), 200)

# endregion