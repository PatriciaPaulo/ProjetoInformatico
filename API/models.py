
from flask import Blueprint, current_app
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import MetaData, Column, Integer, String,Boolean, ForeignKey,Numeric,Text, Date
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
import jwt
metadata = MetaData()
Base = declarative_base(metadata=metadata)
engine = create_engine('sqlite:///spl.db')
db = SQLAlchemy(metadata=metadata)

#region Utilizador
class Utilizador(Base):
    __tablename__="utilizador"
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String(50), nullable=False)
    name = Column(String(128), nullable=False)
    email = Column(String(128), nullable=False)
    admin = Column(Boolean)
    blocked = Column(Boolean)
    deleted_at = Column(String(128))

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
class Atividade(Base):
    __tablename__ = "atividade"
    id = Column(Integer, primary_key=True)
    eventoID = Column(Integer, ForeignKey('evento.id'), nullable=True)
    userID = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    distanciaPercorrida = Column(String(50))
    passos = Column(String(50))
    tipoAtividade = Column(String(50))
    dataInicio = Column(Date)
    dataFim = Column(Date)



    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'eventoID': self.eventoID,
            'distanciaPercorrida': self.distanciaPercorrida,
            'passos': self.passos,
            'dataInicio': self.dataInicio,
            'dataFim': self.dataFim,
            'tipoAtividade': self.tipoAtividade

        }


#endregion

#region LixoNaAtividade
class LixoNaAtividade(Base):
    __tablename__ = "lixo_na_atividade"
    id = Column(Integer, primary_key=True)
    atividadeID = Column(Integer, ForeignKey('atividade.id'), nullable=False)
    lixoID = Column(Integer, ForeignKey('lixo.id'), nullable=False)
    quantidade = Column(String(128), nullable=False)
    medida = Column(String(128), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'atividadeID': self.atividadeID,
            'lixoID': self.lixoID,
            'quantidade': self.quantidade,
            'medida': self.medida
        }
#endregion

#region Lixo
class Lixo(Base):
    __tablename__ = "lixo"
    id = Column(Integer, primary_key=True)
    nome = Column(String(50), unique=True, nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'nome': self.nome

        }


#endregion

#region Equipamento
class Equipamento(Base):
    __tablename__ = "equipamento"
    id = Column(Integer, primary_key=True)
    nome = Column(String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'nome': self.nome

        }
#endregion

#region EquipamentoNoEvento
class EquipamentoNoEvento(Base):
    __tablename__ = "equipamento_no_evento"
    id = Column(Integer, primary_key=True)
    equipamentoID = Column(Integer, ForeignKey('equipamento.id'), nullable=False)
    eventoID = Column(Integer, ForeignKey('evento.id'), nullable=False)
    observacoes = Column(String(50))
    isProvided = Column(Boolean)

    def serialize(self):
        return {
            'id': self.id,
            'equipamentoID': self.equipamentoID,
            'eventoID': self.eventoID,
            'observacoes': self.observacoes,
            'isProvided': self.isProvided

        }
#endregion

#region Evento
class Evento(Base):
    __tablename__ = "evento"
    id = Column(Integer, primary_key=True)
    nome = Column(String(128), nullable=False)
    latitude = Column(Numeric(128), nullable=False)
    longitude = Column(Numeric(128), nullable=False)
    organizador = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    estado = Column(String(50))
    duracao = Column(String(50))
    #todo data inicio
    descricao = Column(String(50))
    acessibilidade = Column(String(50))
    restricoes = Column(String(50))
    tipoLixo = Column(String(50))
    volume = Column(String(50))
    foto = Column(String(50))
    observacoes = Column(String(50))


    def serialize(self):
        return {
            'id': self.id,
            'nome': self.nome,
            'latitude': self.latitude,
            'longitude': self.longitude,
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
class Lixeira(Base):
    __tablename__ = "lixeira"
    id = Column(Integer, primary_key=True)
    nome = Column(String(50), nullable=False)
    latitude  = Column(Numeric(8,6), nullable=False)
    longitude  = Column(Numeric(9,6), nullable=False)
    criador = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    estado = Column(String(50), nullable=False)
    aprovado = Column(Boolean, nullable=False)
    foto = Column(Text,nullable=True)

    def serialize(self):
        return {
            'id': self.id,
            'nome': self.nome,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'criador': self.criador,
            'estado': self.estado,
            'aprovado': self.aprovado,
            'foto': self.foto
        }
#endregion

#region LixeiraEvento
class LixeiraEvento(Base):
    __tablename__ = "lixeira_evento"
    id = Column(Integer, primary_key=True)
    lixeiraID = Column(Integer, ForeignKey('lixeira.id'), nullable=False)
    eventoID = Column(Integer, ForeignKey('evento.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'lixeiraID': self.lixeiraID,
            'eventoID': self.eventoID

        }

#endregion

#region UtilizadorNoEvento
class UtilizadorNoEvento(Base):
    __tablename__ = "utilizador_no_evento"
    id = Column(Integer, primary_key=True)
    userID = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    eventoID = Column(Integer, ForeignKey('evento.id'), nullable=False)
    estado = Column(String(128), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'eventID': self.eventoID,
            'estado': self.estado
        }

#endregion

#region MensagemEvento
class MensagemEvento(Base):
    __tablename__ = "mensagem_evento"
    id = Column(Integer, primary_key=True)
    mensagemID = Column(Integer, ForeignKey('mensagem.id'), nullable=False)
    eventoID = Column(Integer, ForeignKey('evento.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'mensagemID': self.mensagemID,
            'eventoID': self.eventoID

        }
#endregion

#region Amizade
class Amizade(Base):
    __tablename__ = "amizade"
    id = Column(Integer, primary_key=True)
    requestorID = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    addresseeID = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    data = Column(String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'requestorID': self.requestorID,
            'addresseeID': self.addresseeID,
            'data': self.data
        }
#endregion

#region Mensagem
class Mensagem(Base):
    __tablename__ = "mensagem"
    id = Column(Integer, primary_key=True)
    userID = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    message = Column(String(50), nullable=False)
    tipo = Column(String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'message': self.message,
            'tipo': self.tipo
        }
#endregion

#region MensagemIndividual
class MensagemIndividual(Base):
    __tablename__ = "mensagem_individual"
    id = Column(Integer, primary_key=True)
    userID = Column(Integer, ForeignKey('utilizador.id'), nullable=False)
    mensagemID = Column(Integer, ForeignKey('mensagem.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'mensagemID': self.mensagemID
        }
#endregion