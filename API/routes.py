from flask import jsonify, make_response, request, current_app
from werkzeug.security import generate_password_hash,check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, Atividade, Evento, db
import jwt
import datetime

routes_blueprint = Blueprint('routes', __name__,)

api = Api(routes_blueprint)


#region Utilizador
@routes_blueprint.route('/register', methods=['POST'])
def signup_user():
    data = request.get_json()
    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_user = Utilizador(username=data['username'], name=data['name'], email=data['email'], password=hashed_password, admin=False)
    db.session.add(new_user)
    db.session.commit()
    return jsonify({'message': 'registered successfully'})

@routes_blueprint.route('/login', methods=['POST'])
def login_user():
    auth = request.get_json()

    if not auth or not auth['username'] or not auth['password']:
        return make_response('could not verify', 401, {'Authentication': 'login required"'})

    user = Utilizador.query.filter_by(username=auth['username']).first()
    if check_password_hash(user.password,  auth['password']):
        token = jwt.encode(
            {'username': user.username, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=45)},
            current_app.config['SECRET_KEY'], "HS256")

        return jsonify({'token': token})

    return make_response('could not verify', 401, {'Authentication': '"login required"'})


@routes_blueprint.route('/users', methods=['GET'])
def get_all_users():
    users = Utilizador.query.all()
    result = []
    for user in users:
        user_data = {}
        user_data['username'] = user.username
        user_data['name'] = user.name
        user_data['email'] = user.email
        user_data['admin'] = user.admin
        result.append(user_data)
    return jsonify({'users': result})


#endregion

#region Atividade
@routes_blueprint.route('/atividades', methods=['POST'])
@Atividade.token_required
def create_atividade(current_user):
    data = request.get_json()

    new_atividade = Atividade(eventoID=data['eventoID'], userID=current_user.username, distanciaPercorrida=0,
                      passos=0, duracao=0, tipoAtividade=data['tipoAtividade'])
    db.session.add(new_atividade)
    db.session.commit()
    return jsonify({'message': 'new atividade created'})


@routes_blueprint.route('/atividades', methods=['GET'])
@Atividade.token_required
def get_atividades(current_user):
    atividades = Atividade.query.filter_by(userID=current_user.username).all()
    output = []
    for ati in atividades:
        atividade_data = {}
        atividade_data['id'] = ati.id
        atividade_data['eventoID'] = ati.eventoID
        atividade_data['userID'] = ati.userID
        atividade_data['distanciaPercorrida'] = ati.distanciaPercorrida
        atividade_data['passos'] = ati.passos
        atividade_data['duracao'] = ati.duracao
        atividade_data['tipoAtividade'] = ati.tipoAtividade
        output.append(atividade_data)

    return jsonify({'list_of_atividades': output})

@routes_blueprint.route('/atividades/<atividade_id>', methods=['PUT'])
@Atividade.token_required
def update_atividade(current_user,atividade_id):
    atividade = Atividade.query.filter_by(id=atividade_id, userID=current_user.username).first()
    if not atividade:
        return jsonify({'message': 'atividade does not exist'})
    atividade_data = request.get_json()
    atividade.distanciaPercorrida = atividade_data['distanciaPercorrida']
    atividade.passos = atividade_data['passos']
    atividade.duracao = atividade_data['duracao']
    atividade.tipoAtividade = atividade_data['tipoAtividade']

    db.session.commit()
    return jsonify({'message': 'atividade atualizada'})

    return jsonify({'list_of_atividades': output})
#endregion

#region Evento
@routes_blueprint.route('/eventos', methods=['POST'])
@Evento.token_required
def create_evento(current_user):
    data = request.get_json()

    new_evento = Evento(nome=data['nome'], localizacao=data['localizacao'], organizador=current_user.username,
                        estado=data['estado'], duracao=data['duracao'], descricao=data['descricao'],
                        acessibilidade=data['acessibilidade'], restricoes=data['restricoes'], tipoLixo=data['tipoLixo'],
                        volume=data['volume'], foto=data['foto'], observacoes=data['observacoes'],
                        )
    db.session.add(new_evento)
    db.session.commit()
    return jsonify({'message': 'new evento created'})


@routes_blueprint.route('/eventos', methods=['GET'])
@Evento.token_required
def get_eventos(current_user):
    eventos = Evento.query.filter_by(organizador=current_user.username).all()
    output = []
    for evento in eventos:

        evento_data = {}
        evento_data['id'] = evento.id
        evento_data['nome'] = evento.nome
        evento_data['localizacao'] = evento.localizacao
        evento_data['estado'] = evento.estado
        evento_data['duracao'] = evento.duracao
        evento_data['descricao'] = evento.descricao
        evento_data['acessibilidade'] = evento.acessibilidade
        evento_data['restricoes'] = evento.restricoes
        evento_data['tipoLixo'] = evento.tipoLixo
        evento_data['volume'] = evento.volume
        evento_data['foto'] = evento.foto
        evento_data['observacoes'] = evento.observacoes
        output.append(evento_data)

    return jsonify({'list_of_eventos': output})

@routes_blueprint.route('/eventos/<evento_id>', methods=['PUT'])
@Evento.token_required
def update_evento(current_user, evento_id):
    evento = Evento.query.filter_by(id=evento_id, organizador=current_user.username).first()
    if not evento:
        return jsonify({'message': 'evento does not exist'})
    evento_data = request.get_json()

    evento.duracao = evento_data['duracao']
    evento.descricao = evento_data['descricao']
    evento.acessibilidade = evento_data['acessibilidade']
    evento.restricoes = evento_data['restricoes']
    evento.tipoLixo = evento_data['tipoLixo']
    evento.volume = evento_data['volume']
    evento.foto = evento_data['foto']
    evento.observacoes = evento_data['observacoes']

    db.session.commit()
    return jsonify({'message': 'evento atualizada'})

    return jsonify({'list_of_eventos': output})
#endregion
