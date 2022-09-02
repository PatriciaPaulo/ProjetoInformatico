from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import User, Activity, Event, db, GarbageSpot, GarbageSpotInEvent, ActivityType, GarbageInActivity, \
    GarbageType, UnitType, Garbage
from utils import token_required, admin_required, guest
import jwt
import datetime

activity_routes_blueprint = Blueprint('activity_routes', __name__, )
api = Api(activity_routes_blueprint)


# Create Activity for Logged User
@activity_routes_blueprint.route('/activities', methods=['POST'])
@token_required
def start_activity(current_user):
    data = request.get_json()

    new_activity = Activity(eventID=data['eventID'] if data['eventID'] is not None else None,
                            userID=current_user.id,
                            distanceTravelled=0,
                            startDate=datetime.datetime.utcnow(),
                            endDate=None,
                            activityTypeID=0)
    db.session.add(new_activity)
    db.session.commit()

    return make_response(jsonify({'id': new_activity.id, 'message': '200 OK - Activity Started'}), 200)


# Get Logged User Activities
@activity_routes_blueprint.route('/activities', methods=['GET'])
@token_required
def get_activities(current_user):
    activities = db.session.query(Activity).filter_by(userID=current_user.id).all()
    output = []
    for ati in activities:
        activity_data = {}
        activity_data['id'] = ati.id
        activity_data['eventID'] = ati.eventID
        activity_data['userID'] = ati.userID
        activity_data['distanceTravelled'] = ati.distanceTravelled
        activity_data['startDate'] = ati.startDate
        activity_data['endDate'] = ati.endDate
        activity_data['activityType'] = ati.activityTypeID
        output.append(activity_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Activities Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Activities From Logged In User Retrieved'}),
                         200)


# Update Logged User Activity
@activity_routes_blueprint.route('/activities/<activity_id>', methods=['PUT'])
@token_required
def update_activity(current_user, activity_id):
    activity = db.session.query(Activity).filter_by(id=activity_id, userID=current_user.id).first()
    if not activity:
        return make_response(jsonify({'message': '404 NOT OK - No Activity Found'}), 404)

    activity_data = request.get_json()
    activity.distanceTravelled = activity_data['distanceTravelled']
    activity.duration = activity_data['endDate']
    activity.activityType = activity_data['activityType']

    db.session.commit()

    return make_response(jsonify({'message': '200 OK - Activity From Logged In User Updated'}), 200)


# Get All Activity Types
@activity_routes_blueprint.route('/activityTypes', methods=['GET'])
def get_activity_types():
    activitiesT = db.session.query(ActivityType).all()
    output = []

    for activityType in activitiesT:
        type_data = {
            'id': activityType.id,
            'name': activityType.name,
            'icon': activityType.icon
        }

        output.append(type_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Activity Type Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Activity Types Retrieved'}), 200)


# Get Garbage in Activity
@activity_routes_blueprint.route('/activitygarbage/<activity_id>', methods=['GET'])
@token_required
def get_garbage_in_activity(current_user, activity_id):
    activity: Activity = db.session.query(Activity).filter_by(id=activity_id).first()

    if activity.userID != current_user.id:
        return make_response("403 FORBIDDEN - Data doesn't belong to logged user.", 403)

    garbage_in_activity = db.session.query(GarbageInActivity).filter_by(activityID=activity_id).all()
    output = []
    for garbage in garbage_in_activity:
        gb_data = {}

        gb_garbage: Garbage = db.session.query(Garbage).filter_by(id=garbage.garbageID).first()
        gb_garbage_name = gb_garbage.name

        gb_unittype: UnitType = db.session.query(UnitType).filter_by(id=garbage.unitTypeID).first()
        gb_unittype_name = gb_unittype.name

        gb_data['id'] = garbage.id
        gb_data['garbage'] = gb_garbage_name
        gb_data['amount'] = garbage.amount
        gb_data['unit'] = gb_unittype_name

        output.append(gb_data)

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Garbage In Activity Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Garbage From Activity Retrieved'}), 200)


# Update Garbage In Activity
@activity_routes_blueprint.route('/activitygarbage/<activity_id>/update/<garbage_in_activity_id>', methods=['PATCH'])
@token_required
def update_garbageinactivity(current_user, garbage_in_activity_id, activity_id):
    activity: Activity = db.session.query(Activity).filter_by(id=activity_id).first()
    garbageInActivity: GarbageInActivity = db.session.query(GarbageInActivity).filter_by(
        id=garbage_in_activity_id).first()

    if not garbageInActivity:
        return make_response("404 NOT FOUND - Garbage In Activity not found", 404)

    if activity.userID != current_user.id:
        return make_response("403 FORBIDDEN - Data doesn't belong to logged user.", 403)

    if garbageInActivity.activityID != int(activity_id):
        return make_response("403 FORBIDDEN - Data doesn't belong to the provided activity.", 403)

    garbage_data = request.get_json()

    garbageInActivity.amount = garbage_data['amount']
    db.session.commit()

    return make_response(jsonify({'data': [], 'message': '200 OK - Garbage Amount Updated'}), 200)


# Add Garbage to Activity
@activity_routes_blueprint.route('/activitygarbage/<activity_id>/create', methods=['POST'])
@token_required
def create_garbage_in_activity(current_user, activity_id):
    try:
        activity: Activity = db.session.query(Activity).filter_by(id=activity_id).first()

        if activity.userID != current_user.id:
            return make_response("403 FORBIDDEN - Data doesn't belong to logged user.", 403)

        garbage_data = request.get_json()

        new_gb_in_activity = GarbageInActivity(
            activityID=activity.id,
            garbageID=garbage_data['garbageID'],
            amount=garbage_data['amount'],
            unitTypeID=garbage_data['unitTypeID']
        )

        db.session.add(new_gb_in_activity)
        db.session.commit()

        return make_response(jsonify(
            {'data': GarbageInActivity.serialize(new_gb_in_activity), 'message': '200 OK - Added Garbage to Activity'}), 200)

    except:

        return make_response(jsonify({'data': [], 'message': '200 OK - 400 NOT OK - Unknown error!'}), 400)



# Delete Garbage in Activity
@activity_routes_blueprint.route('/activitygarbage/<activity_garbage_id>/delete', methods=['DELETE'])
@token_required
def delete_activity_garbage(current_user, activity_garbage_id):
    try:
        gbia: GarbageInActivity = db.session.query(GarbageInActivity).filter_by(id=activity_garbage_id).first()
        activity: Activity = db.session.query(Activity).filter_by(id=gbia.activityID).first()

        if activity.userID != current_user.id:
            return make_response("403 FORBIDDEN - Data doesn't belong to logged user.", 403)

        if activity.endDate is not None:
            return make_response("403 FORBIDDEN - Activity is already finished.", 403)

        db.session.delete(gbia)
        db.session.commit()

        return make_response("200 OK - Garbage In Activity Deleted", 200)

    except:
        return make_response(jsonify({'message': '400 NOT OK - Unknown error!'}), 400)
