from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot,GarbageSpotInEvent
from utils import token_required, admin_required, guest, name_validation, username_validation, email_validation, \
    password_validation, password_confirmation
import jwt
import datetime

user_routes_blueprint = Blueprint('user_routes', __name__, )
api = Api(user_routes_blueprint)


# Register New User
@user_routes_blueprint.route('/register', methods=['POST'])
def register_user():
    data = request.get_json()

    # Checks if email valid
    if not email_validation(data['email']):
        return make_response("400 Bad Request - Invalid email", 400)

    # Checks if password valid
    if not password_validation(data['password']):
        return make_response("400 Bad Request - Invalid password", 400)

    # Checks if password and password confirmation match
    if not password_confirmation(data['password'], data['passwordConfirmation']):
        return make_response("400 Bad Request - Passwords don\'t match", 400)

    # Checks if email is already registered
    user = db.session.query(User).filter_by(email=data['email']).first()
    if user:
        return make_response("409 Conflict - Email already registered", 409)

    # Hashes password
    hashed_password = generate_password_hash(data['password'], method='sha256')

    # Generates username
    split_email = data['email'].partition['@']
    generated_username = split_email[0]

    new_user = User(username=generated_username, email=data['email'], password=hashed_password)
    db.session.add(new_user)
    db.session.commit()

    return make_response("200 OK - User created successfully", 200)


# User Login
@user_routes_blueprint.route('/login', methods=['POST'])
def login_user():
    auth = request.get_json()

    # Checks if request has email and password parameters
    if not auth or not auth['email'] or not auth['password']:
        return make_response(jsonify({'access_token': None, 'message': '400 Bad Request - Empty fields'}), 400)

    user = db.session.query(User).filter_by(email=auth['email']).first()

    # Checks if user already exists
    if not user:
        return make_response(jsonify({'access_token': "", 'message': '404 Not Found - User doesn\'t exist'}), 404)

    # Checks if user is an admin
    if user.admin:
        return make_response(jsonify({'access_token': "", 'message': '403 Forbidden - Admin accounts not allowed'}), 403)

    # Checks if user is blocked
    if user.blocked:
        return make_response(jsonify({'access_token': "", 'message': '401 Unauthorized - User is blocked'}), 401)

    # Checks if password is correct
    if check_password_hash(user.password, auth['password']):
        token = jwt.encode(
            {'email': user.email, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=1440)},
            current_app.config['SECRET_KEY'], "HS256")

        return make_response(jsonify({'access_token': token, 'message': '200 OK - Login successful'}), 200)

    return make_response(jsonify({'access_token': "", 'message': '500 Internal Server Error'}), 500)


# Get Logged User
@user_routes_blueprint.route('/users/me', methods=['GET'])
@token_required
def get_me(current_user):

    return make_response(jsonify({'data': User.serialize(current_user), 'message': '200 OK - User Retrieved'}), 200)


# Get All Users
@user_routes_blueprint.route('/users', methods=['GET'])
@admin_required
def get_all_users(current_user):
    # Query for all users
    result = []
    users = db.session.query(User).filter_by(deleted_at=None)

    for user in users:
        user_data = {}
        user_data['id'] = user.id
        user_data['username'] = user.username
        user_data['name'] = user.name
        user_data['email'] = user.email
        user_data['admin'] = user.admin
        user_data['blocked'] = user.blocked

        result.append(user_data)

    return make_response(jsonify({'data': result, 'message': '200 OK - All Users Retrieved'}), 200)


# Get users and stats by user
@user_routes_blueprint.route('/users', methods=['GET'])
@token_required
def get_users(current_user):
    # Query for all users
    result = []
    users = db.session.query(User).filter_by(deleted_at=None)

    for user in users:
        if not user.admin:
            if not user.blocked:
                events_participated = db.session.query(Event).filter_by(userID=user.id,status="Finalizado").count()
                from datetime import date

                today = date.today()
                activities_completed = db.session.query(Activity).filter(userID== user.id,endDate <= today).count()

                garbage_spots_created = db.session.query(GarbageSpot).filter_by(userID=user.id,creator=True).count()

                user_data = {}
                user_data['id'] = user.id
                user_data['username'] = user.username
                user_data['name'] = user.name
                user_data['events_participated'] = events_participated
                user_data['activities_completed'] = activities_completed
                user_data['garbage_spots_created'] = garbage_spots_created

                result.append(User.serialize(user_data))

    return make_response(jsonify({'data': result, 'message': '200 OK - All Users Retrieved'}), 200)

# Get User by ID
@user_routes_blueprint.route('/users/<user_id>', methods=['GET'])
@admin_required
def get_user(user_id):
    user = db.session.query(User).filter_by(id=user_id, deleted_at=None).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesn\'t exist'}), 404)

    return make_response(jsonify({'data': User.serialize(user), 'message': '200 OK - User Retrieved'}), 200)

"""
# Update User by Admin
@user_routes_blueprint.route('/users/<user_id>', methods=['PUT'])
@admin_required
def update_user(user_id):
    # Checks if user exist
    user = db.session.query(User).filter_by(id=user_id, deleted_at=None).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesn\'t exist'}), 404)

    user_data = request.get_json()

    user.name = user_data['name']
    user.username = user_data['username']
    user.email = user_data['email']
    db.session.commit()

    return make_response(jsonify({'data': User.serialize(user), 'message': '200 OK - User Updated'}), 200)
"""

#Update User by Logged User
@user_routes_blueprint.route('/users/me', methods=['PUT'])
@token_required
def update_user(current_user):
    # Checks if user exist
    user = db.session.query(User).filter_by(id=current_user.id, deleted_at=None).first()
    if not user:
        return make_response(jsonify({'message': '404 NOT OK - User doesn\'t exist'}), 404)

    user_data = request.get_json()
    user.name = user_data['name']
    user.username = user_data['username']
    user.email = user_data['email']
    db.session.commit()

    return make_response(jsonify({'data': User.serialize(user), 'message': '200 OK - User Updated'}), 200)
