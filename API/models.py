
from flask import Blueprint, current_app
from functools import wraps
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
import jwt
db = SQLAlchemy()





class Utilizador(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String, unique=True, nullable=False)
    password = db.Column(db.String(50))
    name = db.Column(db.String(128), nullable=False)
    email = db.Column(db.String(128), nullable=False)
    admin = db.Column(db.Boolean)


    def serialize(self):
        return {
            'id': self.id,
            'username': self.username,
            'name': self.name,
            'email': self.email,
            'admin': self.admin
        }



def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'x-access-tokens' in request.headers:
            token = request.headers['x-access-tokens']

        if not token:
            return jsonify({'message': 'a valid token is missing'})
        try:
            data = jwt.decode(token, current_app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = Utilizador.query.filter_by(username=data['username']).first()
        except:
            return jsonify({'message': 'token is invalid'})

        return f(current_user, *args, **kwargs)

    return decorator
