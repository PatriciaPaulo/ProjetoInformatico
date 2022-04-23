from functools import wraps

import jwt
from flask import Flask, jsonify, request, current_app

from models import Utilizador


def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None

        if 'authorization' in request.headers:
            token = request.headers['authorization']
        if not token:
            return jsonify({'message': 'a valid token is missing'})
        try:
            data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = Utilizador.query.filter_by(username=data['username']).first()

        except:
            return jsonify({'message': 'token is invalid'})

        return f(current_user, *args, **kwargs)

    return decorator


def admin_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None

        if 'authorization' in request.headers:
            token = request.headers['authorization']
        if not token:
            return jsonify({'message': 'a valid token is missing'})
        try:
            data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = Utilizador.query.filter_by(username=data['username']).first()
            if not current_user.admin:
                return jsonify({'message': 'user is not an admin'})
        except:
            return jsonify({'message': 'token is invalid'})

        return f(current_user, *args, **kwargs)

    return decorator