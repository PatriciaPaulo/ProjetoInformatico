
from flask import Blueprint, current_app
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
import jwt
db = SQLAlchemy()

#TODO falta tabelas de equipamento e mensagens

#region Utilizador
class Utilizador(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String, unique=True, nullable=False)
    password = db.Column(db.String(50))
    name = db.Column(db.String(128), nullable=False)
    email = db.Column(db.String(128), nullable=False)
    admin = db.Column(db.Boolean)
    blocked = db.Column(db.Boolean)

    def serialize(self):
        return {
            'id': self.id,
            'username': self.username,
            'name': self.name,
            'email': self.email,
            'admin': self.admin,
            'blocked': self.blocked
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


#endregion

#region Lixo
class Lixo(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    nome = db.Column(db.String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'nome': self.nome

        }


#endregion

#region LixoNaAtividade
class LixoNaAtividade(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    atividadeID = db.Column(db.Integer, db.ForeignKey('atividade.id'), nullable=False)
    lixoID = db.Column(db.Integer, db.ForeignKey('lixo.id'), nullable=False)
    quantidade = db.Column(db.String(128), nullable=False)
    medida = db.Column(db.String(128), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'atividadeID': self.atividadeID,
            'lixoID': self.lixoID,
            'quantidade': self.quantidade,
            'medida': self.medida
        }
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


#endregion

# region Lixeira
class Lixeira(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    localizacao = db.Column(db.String(128), nullable=False)
    criador = db.Column(db.Integer, db.ForeignKey('utilizador.id'), nullable=False)
    estado = db.Column(db.String(50), nullable=False)
    aprovado = db.Column(db.Boolean, nullable=False)
    foto = db.Column(db.Text,nullable=True)

    def serialize(self):
        return {
            'id': self.id,
            'localizacao': self.localizacao,
            'criador': self.criador,
            'estado': self.estado,
            'aprovado': self.aprovado,
            'foto': self.foto
        }
#endregion

#region LixeiraEvento
class LixeiraEvento(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    lixeiraID = db.Column(db.Integer, db.ForeignKey('lixeira.id'), nullable=False)
    eventID = db.Column(db.Integer, db.ForeignKey('evento.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'lixeiraID': self.lixeiraID,
            'eventID': self.eventID

        }

#endregion

#region UtilizadorNoEvento
class UtilizadorNoEvento(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    userID = db.Column(db.Integer, db.ForeignKey('utilizador.id'), nullable=False)
    eventID = db.Column(db.Integer, db.ForeignKey('evento.id'), nullable=False)
    estado = db.Column(db.String(128), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'eventID': self.eventID,
            'estado': self.estado
        }

#endregion