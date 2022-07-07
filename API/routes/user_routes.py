from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, Atividade, Evento, db, Lixeira,LixeiraEvento
from utils import token_required,admin_required,guest
import jwt
import datetime

user_routes_blueprint = Blueprint('user_routes', __name__, )
api = Api(user_routes_blueprint)

# Register New User
@user_routes_blueprint.route('/register', methods=['POST'])
def register_user():
    data = request.get_json()
    # todo
    # Checks if usernamename valid
    # Checks if name valid
    # Checks if email valid
    # Checks if password valid

    # Checks if password and password confirmation match
    if data['password'] != data['passwordConfirmation']:
        return make_response('Passwords don\'t match', 400)

    # Checks if user already exists
    user = db.session.query(Utilizador).filter_by(username=data['username']).first()
    if user:
        return make_response('User already exists', 409)

    # Hashes password
    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_user = Utilizador(username=data['username'], name=data['name'], email=data['email'], password=hashed_password,admin=False, blocked=False)
    db.session.add(new_user)
    db.session.commit()

    return make_response('User created successfully', 200)


# User Login
@user_routes_blueprint.route('/login', methods=['POST'])
def login_user():
    auth = request.get_json()
    # Checks if requests has username and password parameters
    if not auth or not auth['email'] or not auth['password']:
        return make_response(jsonify({'access_token': "",'message': 'Bad request','status':400}), 400)

    # Checks if user exists
    user = db.session.query(Utilizador).filter_by(email=auth['email']).first()
    if not user:
        return make_response(jsonify({'access_token': "",'message': 'User doesn\'t exist','status':404}), 404)

    # Checks if user is an admin
    if user.admin:
        return make_response(jsonify({'access_token': "",'message': 'Can not login with admin account!','status':401}), 401)

    # Checks if user is blocked
    if user.blocked:
        return make_response(jsonify({'access_token': "",'message': 'Unknown Error, try again!','status':400}), 400)

    # Checks if password is correct
    if check_password_hash(user.password, auth['password']):
        # If correct returns makes and returns token
        token = jwt.encode(
            {'email': user.email, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=45)},
            current_app.config['SECRET_KEY'], "HS256")

        return make_response(jsonify({'access_token': token,'message':'logged in','status': 200}))

    return make_response(jsonify({'access_token': "",'message': 'Unknown Error, try again!','status':400}), 400)

# Get Logged User
@user_routes_blueprint.route('/users/me', methods=['GET'])
@token_required
def get_me(current_user):
    resp = make_response(jsonify({'data': Utilizador.serialize(current_user)}), 200)  # here you could use make_response(render_template(...)) too
    resp.headers['Access-Control-Allow-Origin'] = '*'
    return resp

# Get All Users
@user_routes_blueprint.route('/users', methods=['GET'])
@admin_required
def get_all_users(current_user):
    #Query for all users
    result = []
    users = db.session.query(Utilizador).filter_by(deleted_at=None)

    for user in users:
        user_data = {}
        user_data['id'] = user.id
        user_data['username'] = user.username
        user_data['name'] = user.name
        user_data['email'] = user.email
        user_data['admin'] = user.admin
        user_data['blocked'] = user.blocked
        result.append(user_data)


    return make_response(jsonify({'data': result}), 200)

# Get User by ID
@user_routes_blueprint.route('/users/<user_id>', methods=['GET'])
@admin_required
def get_user(user_id):
    #find user
    user = db.session.query(Utilizador).filter_by(id=user_id,deleted_at=None).first()
    if not user:
        return make_response(jsonify({'message': "User doesnt exist"}), 404)

    return make_response(jsonify({'data': Utilizador.serialize(user)}), 200)


# Update User by Admin
@user_routes_blueprint.route('/users/<user_id>', methods=['PUT'])
@admin_required
def update_user(current_user, user_id):
    # Checks if user exist
    user = db.session.query(Utilizador).filter_by(id=user_id, deleted_at=None).first()
    print(user_id)
    print(user)
    if not user:
        return make_response('User doesn\'t exist', 404)

    user_data = request.get_json()

    user.name = user_data['name']
    user.username = user_data['username']
    user.email = user_data['email']
    db.session.commit()
    return make_response(jsonify({'data': Utilizador.serialize(user)}), 200)


