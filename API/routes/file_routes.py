import os.path

from flask import Blueprint, request, flash, make_response, current_app
from flask_restful import Api
from werkzeug.utils import redirect, secure_filename

from models import PictureInActivity, db, PictureInGarbageSpot, User
from utils import token_required

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

file_routes_blueprint = Blueprint('file_routes', __name__, )
api = Api(file_routes_blueprint)


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


# Upload Profile File
@file_routes_blueprint.route('/uploadProfileFile', methods=['POST'])
@token_required
def upload_profile_file(current_user):
    # Check if post request has file
    if 'file' not in request.files:
        flash('No file part')
        return make_response("400 BAD REQUEST - A file must be attached", 400)

    file = request.files['file']
    if file.filename == '':
        flash('No selected file')
        return make_response("400 BAD REQUEST - A file must be attached", 400)

    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)

        upload_folder = os.path.join(current_app.config['UPLOAD_FOLDER'], "profiles")

        user = db.session.query(User).filter_by(id=current_user.id)
        if not user:
            return make_response("500 INTERNAL SERVER ERROR - User not found", 500)
        user.icon = filename

        db.session.commit()

        file.save(os.path.join(upload_folder, filename))
        return make_response("200 OK - Profile file saved", 200)


# Upload Activity File
@file_routes_blueprint.route('/uploadActivityFile', methods=['POST'])
@token_required
def upload_activity_file(activity_id):
    # Check if post request has file
    if 'file' not in request.files:
        return make_response("400 BAD REQUEST - No file part", 400)

    file = request.files['file']
    if file.filename == '':
        return make_response("400 BAD REQUEST - No selected file", 400)

    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)

        upload_folder = os.path.join(current_app.config['UPLOAD_FOLDER'], "activities")

        new_activity_file = PictureInActivity(
            activityID=id,
            path=filename
        )

        db.session.add(new_activity_file)
        db.session.commit()

        file.save(os.path.join(upload_folder, filename))
        return make_response("200 OK - Activity file saved", 200)


# Upload Activity File
@file_routes_blueprint.route('/uploadActivityFile', methods=['POST'])
@token_required
def upload_activity_file(activity_id):
    # Check if post request has file
    if 'file' not in request.files:
        return make_response("400 BAD REQUEST - No file part", 400)

    file = request.files['file']
    if file.filename == '':
        return make_response("400 BAD REQUEST - No selected file", 400)

    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)

        upload_folder = os.path.join(current_app.config['UPLOAD_FOLDER'], "garbagespots")
        new_garbagespot_file = PictureInGarbageSpot(
            garbageSpotID=id,
            path=filename
        )

        db.session.add(new_garbagespot_file)
        db.session.commit()

        file.save(os.path.join(upload_folder, filename))
        return make_response("200 OK - Activity file saved", 200)

