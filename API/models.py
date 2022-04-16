
from flask import Blueprint, current_app
from functools import wraps
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
import jwt
db = SQLAlchemy()



#region Utilizador

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




#endregion

#region Atividade
class Atividade(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    eventoID = db.Column(db.Integer, db.ForeignKey('evento.id'), nullable=True)
    userID = db.Column(db.Integer, db.ForeignKey('utilizador.id'), nullable=False)
    distanciaPercorrida = db.Column(db.String(50))
    passos = db.Column(db.String(50))
    duracao = db.Column(db.String(50))
    tipoAtividade = db.Column(db.String(50))


    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'eventoID': self.eventoID,
            'distanciaPercorrida': self.distanciaPercorrida,
            'passos': self.passos,
            'duracao': self.duracao,
            'tipoAtividade': self.tipoAtividade

        }

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
#endregion


#region Evento
class Evento(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    nome = db.Column(db.String(128), nullable=False)
    localizacao = db.Column(db.String(128), nullable=False)
    organizador = db.Column(db.Integer, db.ForeignKey('utilizador.id'), nullable=False)
    estado = db.Column(db.String(50))
    duracao = db.Column(db.String(50))
    descricao = db.Column(db.String(50))
    acessibilidade = db.Column(db.String(50))
    restricoes = db.Column(db.String(50))
    tipoLixo = db.Column(db.String(50))
    #????? wat is volume
    volume = db.Column(db.String(50))
    foto = db.Column(db.String(50))
    observacoes = db.Column(db.String(50))


    def serialize(self):
        return {
            'id': self.id,
            'nome': self.nome,
            'localizacao': self.localizacao,
            'organizador': self.organizador,
            'estado': self.estado,
            'duracao': self.duracao,
            'acessibilidade': self.acessibilidade,
            'restricoes': self.restricoes,
            'tipoLixo': self.tipoLixo,
            'volume': self.volume,
            'foto': self.foto,
            'observacoes': self.observacoes

        }

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
#endregion




#region Lixeira

#endregion