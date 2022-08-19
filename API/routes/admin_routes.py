from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, db
from utils import admin_required
import jwt
import datetime

admin_routes_blueprint = Blueprint('admin_routes', __name__, )
api = Api(admin_routes_blueprint)


# Register New Admins
@admin_routes_blueprint.route('/registerAdmin', methods=['POST'])
@admin_required
def create_admin(current_user):
    data = request.get_json()

    # Checks if admin already exists
    admin = db.session.query(User).filter_by(username=data['username']).first()
    if admin:
        return make_response('Admin already exists', 409)

    # TODO
    # Checks if username valid
    # Checks if name valid
    # Checks if email valid
    # Checks if password valid
    if len(data['username']) < 3:
        return make_response(jsonify({'message': '400 NOT OK - Username too short!'}), 400)
    if len(data['name']) < 3:
        return make_response(jsonify({'message': '400 NOT OK - Name too short!'}), 400)
    if len(data['password']) < 3:
        return make_response(jsonify({'message': '400 NOT OK - Password too short!'}), 400)
    if len(data['email']) < 3:
        return make_response(jsonify({'message': '400 NOT OK - Password too short!'}), 400)

    # Checks if password and password confirmation match
    if data['password'] != data['passwordConfirmation']:
        return make_response(jsonify({'message': '400 NOT OK - Passwords don\'t match!'}), 400)

    # Hashes password
    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_admin = User(username=data['username'], name=data['name'], email=data['email'], password=hashed_password,
                     admin=True, blocked=False)
    db.session.add(new_admin)
    db.session.commit()
    return make_response('Admin created successfully', 200)


# Admin Login
@admin_routes_blueprint.route('/loginBackOffice', methods=['POST'])
def login_admin():
    auth = request.get_json()
    # Checks if requests has username and password parameters
    if not auth or not auth['email'] or not auth['password']:
        return make_response('Bad request', 400)

    # Checks if user exists
    admin = db.session.query(User).filter_by(email=auth['email']).first()
    if not admin:
        return make_response('Admin doesn\'t exist', 404)

    # Checks if user is an admin
    if not admin.admin:
        return make_response('user not an admin', 401)

    # Checks if admin is blocked
    if admin.blocked:
        return make_response('You are blocked!', 403)

    # Checks if password is correct
    if check_password_hash(admin.password, auth['password']):
        # If correct returns makes and returns token
        token = jwt.encode(
            {'email': admin.email, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=45)},
            current_app.config['SECRET_KEY'], "HS256")

        return make_response(jsonify({'access_token': token}), 200)

    return make_response('Unknown Error, try again!', 400)


# Block/Unblock Users
@admin_routes_blueprint.route('/users/<user_id>/block', methods=['PATCH'])
@admin_required
def block_user(current_user, user_id):
    # Checks if user to be blocked exists
    user = db.session.query(User).filter_by(id=user_id).first()
    if not user:
        return make_response('User doesn\'t exist', 404)

    user_data = request.get_json()
    user.blocked = user_data['blocked']
    db.session.commit()

    # message based on action (block or unblock)
    if user.blocked:
        return make_response('User blocked successfully', 200)
    else:
        return make_response('User unblocked successfully', 200)


# Delete User
@admin_routes_blueprint.route('/users/<user_id>', methods=['DELETE'])
@admin_required
def delete_user(current_user, user_id):
    # Checks if user exists
    user = db.session.query(User).filter_by(id=user_id).first()
    if not user:
        return make_response('User doesn\'t exist', 404)
    # Checks if user is deleting himself
    if current_user.id == user_id:
        return make_response('can not delete yourself', 400)

    user.deleted_at = datetime.datetime.utcnow()
    db.session.commit()
    return make_response('User deleted successfully', 200)
