from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, Atividade, Evento, db, Lixeira,LixeiraEvento
from utils import token_required,admin_required,guest
import jwt
import datetime

activity_routes_blueprint = Blueprint('activity_routes', __name__, )
api = Api(activity_routes_blueprint)

# Create Activity for Logged User
@activity_routes_blueprint.route('/atividades', methods=['POST'])
@token_required
def create_atividade(current_user):
    data = request.get_json()

    new_atividade = Atividade(eventoID=data['eventoID'], userID=current_user.username, distanciaPercorrida=0,passos=0, dataInicio=datetime.datetime.utcnow(), dataFim=None,tipoAtividade=data['tipoAtividade'])
    db.session.add(new_atividade)
    db.session.commit()
    return make_response(jsonify("message': 'new atividade created'"), 200)


# Get Logged User Activities
@activity_routes_blueprint.route('/atividades', methods=['GET'])
@token_required
def get_atividades(current_user):
    atividades = db.session.query(Atividade).filter_by(userID=current_user.username).all()
    output = []
    for ati in atividades:
        atividade_data = {}
        atividade_data['id'] = ati.id
        atividade_data['eventoID'] = ati.eventoID
        atividade_data['userID'] = ati.userID
        atividade_data['distanciaPercorrida'] = ati.distanciaPercorrida
        atividade_data['passos'] = ati.passos
        atividade_data['dataInicio'] = ati.dataInicio
        atividade_data['dataFim'] = ati.dataFim
        atividade_data['tipoAtividade'] = ati.tipoAtividade
        output.append(atividade_data)

    return make_response(jsonify({'data': output}), 200)


# Update Logged User Activity
@activity_routes_blueprint.route('/atividades/<atividade_id>', methods=['PUT'])
@token_required
def update_atividade(current_user, atividade_id):
    atividade = db.session.query(Atividade).filter_by(id=atividade_id, userID=current_user.username).first()
    if not atividade:
        return make_response(jsonify({'message': 'atividade does not exist'}), 400)

    atividade_data = request.get_json()
    atividade.distanciaPercorrida = atividade_data['distanciaPercorrida']
    atividade.passos = atividade_data['passos']
    atividade.duracao = atividade_data['dataFim']
    atividade.tipoAtividade = atividade_data['tipoAtividade']

    db.session.commit()

    return make_response(jsonify({'message': 'atividade atualizada'}), 200)