from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot,GarbageSpotInEvent
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

    new_atividade = Activity(eventoID=data['eventID'], userID=current_user.username, distanciaPercorrida=0, passos=0, dataInicio=datetime.datetime.utcnow(), dataFim=None, tipoAtividade=data['activityType'])
    db.session.add(new_atividade)
    db.session.commit()
    return make_response(jsonify("message': 'new atividade created'"), 200)


# Get Logged User Activities
@activity_routes_blueprint.route('/atividades', methods=['GET'])
@token_required
def get_atividades(current_user):
    atividades = db.session.query(Activity).filter_by(userID=current_user.username).all()
    output = []
    for ati in atividades:
        atividade_data = {}
        atividade_data['id'] = ati.id
        atividade_data['eventID'] = ati.eventID
        atividade_data['userID'] = ati.userID
        atividade_data['distanceTravelled'] = ati.distanceTravelled
        atividade_data['steps'] = ati.steps
        atividade_data['startDate'] = ati.startDate
        atividade_data['endDate'] = ati.endDate
        atividade_data['activityType'] = ati.activityType
        output.append(atividade_data)

    return make_response(jsonify({'date': output}), 200)


# Update Logged User Activity
@activity_routes_blueprint.route('/atividades/<atividade_id>', methods=['PUT'])
@token_required
def update_atividade(current_user, atividade_id):
    atividade = db.session.query(Activity).filter_by(id=atividade_id, userID=current_user.username).first()
    if not atividade:
        return make_response(jsonify({'message': 'atividade does not exist'}), 400)

    atividade_data = request.get_json()
    atividade.distanceTravelled = atividade_data['distanceTravelled']
    atividade.steps = atividade_data['steps']
    atividade.duration = atividade_data['endDate']
    atividade.activityType = atividade_data['activityType']

    db.session.commit()

    return make_response(jsonify({'message': 'atividade atualizada'}), 200)