from flask import jsonify, make_response, request
from flask_restful import Api
from flask import Blueprint
from models import db, Garbage, Equipment
from utils import token_required

equipment_routes_blueprint = Blueprint('equipment_routes', __name__, )
api = Api(equipment_routes_blueprint)

# Create Equipment
@equipment_routes_blueprint.route('/equipments', methods=['POST'])
@token_required
def create_equipment(current_user):
    data = request.get_json()

    equipment = db.session.query(Equipment).filter_by(name=data['name']).first()
    if equipment:
              return make_response(jsonify({'message': '409 NOT OK - Equipment already exists!'}), 409)


    new_equipment = Equipment(name=data['name'])


    db.session.add(new_equipment)
    db.session.commit()

    return make_response(jsonify({'data': Equipment.serialize(new_garbageType), 'message': '200 OK - Garbage Type Created'}), 200)\



# get_all_equipment
@equipment_routes_blueprint.route('/equipments', methods=['GET'])
def get_all_equipment():

    equipments = db.session.query(Equipment).all()

    output = []
    for equipment in equipments:
        equipment_data = {'id': equipment.id, 'name': equipment.name}

        output.append(equipment_data)

    if len(output) == 0:
        return make_response(jsonify({'data':[],'message':'404 NOT OK - No Equipment Type Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Equipment Retrieved'}), 200)
