import os.path

from flask import Blueprint, request, flash, make_response, current_app
from flask_restful import Api
from werkzeug.utils import redirect, secure_filename

from models import PictureInActivity, db, PictureInGarbageSpot, User

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

file_routes_blueprint = Blueprint('file_routes', __name__, )
api = Api(file_routes_blueprint)


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


# Upload File
@file_routes_blueprint.route('/uploadFile', methods=['POST'])
def upload_file():
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

        folder_code = filename[0:3]

        split_filename = filename.split("-")[0]

        try:
            id = split_filename[3:]
        except:
            return make_response("500 INTERNAL SERVER ERROR - File name without source id", 500)

        print("FILENAME: " + filename + "\nFOLDER CODE: " + folder_code + "\nID: " + id)

        if folder_code == "201":
            upload_folder = os.path.join(current_app.config['UPLOAD_FOLDER'], "activities")
            new_activity_file = PictureInActivity(
                activityID=id,
                path=filename
            )

            db.session.add(new_activity_file)
            db.session.commit()

        elif folder_code == "202":
            upload_folder = os.path.join(current_app.config['UPLOAD_FOLDER'], "garbagespots")
            new_garbagespot_file = PictureInGarbageSpot(
                garbageSpotID=id,
                path=filename
            )

            db.session.add(new_garbagespot_file)
            db.session.commit()

        elif folder_code == "203":
            upload_folder = os.path.join(current_app.config['UPLOAD_FOLDER'], "profiles")

            user = db.session.query(User).filter_by(id=id)
            if not user:
                return make_response("500 INTERNAL SERVER ERROR - User id not found", 500)
            user.icon = filename

            db.session.commit()

        else:
            return make_response("500 INTERNAL SERVER ERROR - Invalid file name", 500)

        file.save(os.path.join(upload_folder, filename))
        return make_response("200 OK - File saved", 200)
