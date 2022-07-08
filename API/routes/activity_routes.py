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
@activity_routes_blueprint.route('/activitys', methods=['POST'])
@token_required
def create_activity(current_user):
    data = request.get_json()

    new_activity = Activity(eventID=data['eventID'], userID=current_user.username, distanceTravelled=0, steps=0, startDate=datetime.datetime.utcnow(), endDate=None, activityType=data['activityType'])
    db.session.add(new_activity)
    db.session.commit()
    return make_response(jsonify("message': 'new activity created'"), 200)


# Get Logged User Activities
@activity_routes_blueprint.route('/activitys', methods=['GET'])
@token_required
def get_activitys(current_user):
    activitys = db.session.query(Activity).filter_by(userID=current_user.username).all()
    output = []
    for ati in activitys:
        activity_data = {}
        activity_data['id'] = ati.id
        activity_data['eventID'] = ati.eventID
        activity_data['userID'] = ati.userID
        activity_data['distanceTravelled'] = ati.distanceTravelled
        activity_data['steps'] = ati.steps
        activity_data['startDate'] = ati.startDate
        activity_data['endDate'] = ati.endDate
        activity_data['activityType'] = ati.activityType
        output.append(activity_data)

    return make_response(jsonify({'date': output}), 200)


# Update Logged User Activity
@activity_routes_blueprint.route('/activitys/<activity_id>', methods=['PUT'])
@token_required
def update_activity(current_user, activity_id):
    activity = db.session.query(Activity).filter_by(id=activity_id, userID=current_user.username).first()
    if not activity:
        return make_response(jsonify({'message': 'activity does not exist'}), 400)

    activity_data = request.get_json()
    activity.distanceTravelled = activity_data['distanceTravelled']
    activity.steps = activity_data['steps']
    activity.duration = activity_data['endDate']
    activity.activityType = activity_data['activityType']

    db.session.commit()

    return make_response(jsonify({'message': 'activity atualizada'}), 200)