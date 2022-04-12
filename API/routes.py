from flask import jsonify, make_response, request, current_app
from werkzeug.security import generate_password_hash,check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, db
import jwt
import datetime


routes_blueprint = Blueprint('routes', __name__,)

api = Api(routes_blueprint)



@routes_blueprint.route('/register', methods=['POST'])
def signup_user():
    data = request.get_json()
    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_user = Utilizador(username=data['username'], name=data['name'], email=data['email'], password=hashed_password, admin=False)
    db.session.add(new_user)
    db.session.commit()
    return jsonify({'message': 'registered successfully'})

@routes_blueprint.route('/login', methods=['POST'])
def login_user():
    auth = request.get_json()

    if not auth or not auth['username'] or not auth['password']:
        return make_response('could not verify', 401, {'Authentication': 'login required"'})

    user = Utilizador.query.filter_by(username=auth['username']).first()
    if check_password_hash(user.password,  auth['password']):
        token = jwt.encode(
            {'username': user.username, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=45)},
            current_app.config['SECRET_KEY'], "HS256")

        return jsonify({'token': token})

    return make_response('could not verify', 401, {'Authentication': '"login required"'})


@routes_blueprint.route('/users', methods=['GET'])
def get_all_users():
    users = Utilizador.query.all()
    result = []
    for user in users:
        user_data = {}
        user_data['username'] = user.username
        user_data['name'] = user.name
        user_data['email'] = user.email
        user_data['admin'] = user.admin
        result.append(user_data)
    return jsonify({'users': result})
