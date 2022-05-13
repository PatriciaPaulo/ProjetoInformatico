from functools import wraps

import jwt
from flask import Flask, jsonify, request, make_response,current_app
from models import Utilizador, db


def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'authorization' in request.headers:
            token = request.headers['authorization']
        if not token:
            return make_response(jsonify({'message': 'a valid token is missing'}), 400)

        try:
            data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = db.session.query(Utilizador).filter_by(username=data['username']).first()

        except Exception as ex:
            print(ex)
            return make_response(jsonify({'message': 'token is invalid'}), 400)

        return f(current_user, *args, **kwargs)

    return decorator


def admin_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None

        if 'authorization' in request.headers:
            token = request.headers['authorization']
        if not token:
            return make_response(jsonify({'message': 'a valid token is missing'}), 400)

        try:
            data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = db.session.query(Utilizador).filter_by(username=data['username']).first()
            if not current_user.admin:
                return make_response(jsonify({'message': 'user is not an admin'}), 403)

        except Exception as ex:
            print(ex)
            return make_response(jsonify({'message': 'token is invalid'}), 400)


        return f(current_user, *args, **kwargs)

    return decorator