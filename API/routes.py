from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, Atividade, Evento, db, Lixeira,LixeiraEvento
from utils import token_required,admin_required,guest
import jwt
import datetime


routes_blueprint = Blueprint('s', __name__, )
api = Api(routes_blueprint)
#todo
#api = Flask(routes_blueprint)


