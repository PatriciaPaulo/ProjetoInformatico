from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, Friendship
from utils import token_required, admin_required, guest, name_validation, username_validation, email_validation,  password_validation, password_confirmation
import jwt
from datetime import date
friends_routes_blueprint = Blueprint('friends_routes', __name__, )
api = Api(friends_routes_blueprint)


# Register New User
@friends_routes_blueprint.route('/friends', methods=['POST'])
@token_required
def friend_request(current_user):
    data = request.get_json()

    person = db.session.query(User).filter_by(id=data).first()

    if not person:
        return make_response(jsonify({'message': '404 NOT OK - User doesnt exist!'}), 404)

    friend = db.session.query(Friendship).filter_by(requestorID=current_user.id,addresseeID=data).first()

    if friend:
        return make_response(jsonify({'message': '409 NOT OK - You are already friends with this user!'}), 409)


    today = date.today()
    new_friendship = Friendship(requestorID=current_user.id,addresseeID=data,date=today)

    db.session.add(new_friendship)
    db.session.commit()

    return make_response(jsonify({'message': '200 OK - Friendship Created'}), 200)
