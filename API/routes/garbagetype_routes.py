from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import db, Garbage
from utils import token_required,admin_required,guest
import jwt
import datetime


garbagetype_routes_blueprint = Blueprint('garbagetype_routes', __name__, )
api = Api(garbagetype_routes_blueprint)

# Create Garbage Spot
@garbagetype_routes_blueprint.route('/garbageTypes', methods=['POST'])
@guest
def create_garbageType(current_user):
    data = request.get_json()
    # Checks if garbageSpot with same coordinates exists
    garbageType = db.session.query(GarbageType).filter_by(name=data['name']).first()
    if garbageType:
              return make_response(jsonify({'message': '409 NOT OK - GarbageType already exists!'}), 409)


    new_garbageType = GarbageType(name=data['name'])


    db.session.add(new_garbageType)
    db.session.commit()

    return make_response(jsonify({'data': GarbageType.serialize(new_garbageType), 'message': '200 OK - Garbage Type Created'}), 200)


# Get All Garbage Types
@garbagetype_routes_blueprint.route('/garbageTypes', methods=['GET'])
def get_all_garbageSpots():

    garbageTypes= db.session.query(Garbage).all()

    output = []
    for garbageType in garbageTypes:
        garbageType_data = {}
        garbageType_data['id'] = garbageType.id
        garbageType_data['name'] = garbageType.name


        output.append(garbageType_data)

    if len(output) == 0:
        return make_response(jsonify({'data':[],'message':'404 NOT OK - No Garbage Type Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Garbage Types Retrieved'}), 200)
