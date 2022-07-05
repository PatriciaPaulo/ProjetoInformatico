from flask import jsonify, make_response, request, current_app, Flask
from werkzeug.security import generate_password_hash, check_password_hash
from flask_restful import Api
from flask import Blueprint
from models import Utilizador, Atividade, Evento, db, Lixeira,LixeiraEvento
from utils import token_required,admin_required,guest
import jwt
import datetime


routes_blueprint = Blueprint('routes', __name__, )
api = Api(routes_blueprint)
#todo
#api = Flask(routes_blueprint)
# region backoffice
#registers only admins by admins
@routes_blueprint.route('/registerAdmin', methods=['POST'])
@admin_required
def create_admin(current_user):
    data = request.get_json()

    #Checks if admin already exists
    admin = db.session.query(Utilizador).filter_by(username=data['username']).first()
    if admin:
        return make_response('Admin already exists', 409)

    # todo
    # Checks if usernamename valid
    # Checks if name valid
    # Checks if email valid
    # Checks if password valid

    #Checks if password and password confirmation match
    if data['password'] != data['passwordConfirmation']:
        return make_response('Passwords don\'t match', 400)

    #Hashes password
    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_admin = Utilizador(username=data['username'], name=data['name'], email=data['email'], password=hashed_password,admin=True, blocked=False)
    db.session.add(new_admin)
    db.session.commit()
    return make_response('Admin created successfully', 200)

#login for admins
@routes_blueprint.route('/loginBackOffice', methods=['POST'])
def login_admin():
    auth = request.get_json()
    #Checks if requests has username and password parameters
    if not auth or not auth['email'] or not auth['password']:
        return make_response('Bad request', 400)

    #Checks if user exists
    admin = db.session.query(Utilizador).filter_by(email=auth['email']).first()
    if not admin:
        return make_response('Admin doesn\'t exist', 404)

    #Checks if user is an admin
    if not admin.admin:
        return make_response('user not an admin', 401)

    #Checks if admin is blocked
    if admin.blocked:
        return make_response('You are blocked!', 403)

    #Checks if password is correct
    if check_password_hash(admin.password, auth['password']):
        #If correct returns makes and returns token
        token = jwt.encode(
            {'email': admin.email, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=45)},
            current_app.config['SECRET_KEY'], "HS256")

        return make_response(jsonify({'access_token': token}), 200)

    return make_response('Unknown Error, try again!', 400)

#Blocks or unblocks user by admin
@routes_blueprint.route('/users/<user_id>/block', methods=['PATCH'])
@admin_required
def bloquear_user(current_user,user_id):
    #Checks if user to be blocked exists
    user = db.session.query(Utilizador).filter_by(id=user_id).first()
    if not user:
        return make_response('User doesn\'t exist', 404)

    user_data = request.get_json()
    user.blocked = user_data['blocked']
    db.session.commit()
    #message based on action (block or unblock)
    if user.blocked:
        return make_response('User blocked successfully', 200)
    else:
        return make_response('User unblocked successfully', 200)


#Deletes user by admin
@routes_blueprint.route('/users/<user_id>', methods=['DELETE'])
@admin_required
def delete_user(current_user, user_id):
    #Checks if user exists
    user = db.session.query(Utilizador).filter_by(id=user_id).first()
    if not user:
        return make_response('User doesn\'t exist', 404)
    #Checks if user is deleting himself
    if current_user.id == user_id:
        return make_response('can not delete yourself', 400)

    user.deleted_at = datetime.datetime.utcnow()
    db.session.commit()
    return make_response('User deleted successfully', 200)


#endregion

# region Utilizador
#Register user for mobile app
@routes_blueprint.route('/register', methods=['POST'])
def register_user():
    data = request.get_json()
    # todo
    # Checks if usernamename valid
    # Checks if name valid
    # Checks if email valid
    # Checks if password valid

    # Checks if password and password confirmation match
    if data['password'] != data['passwordConfirmation']:
        return make_response('Passwords don\'t match', 400)

    # Checks if user already exists
    user = db.session.query(Utilizador).filter_by(username=data['username']).first()
    if user:
        return make_response('User already exists', 409)

    # Hashes password
    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_user = Utilizador(username=data['username'], name=data['name'], email=data['email'], password=hashed_password,admin=False, blocked=False)
    db.session.add(new_user)
    db.session.commit()

    return make_response('User created successfully', 200)


@routes_blueprint.route('/login', methods=['POST'])
def login_user():
    auth = request.get_json()
    # Checks if requests has username and password parameters
    if not auth or not auth['email'] or not auth['password']:
        return make_response(jsonify({'access_token': "",'message': 'Bad request','status':400}))

    # Checks if user exists
    user = db.session.query(Utilizador).filter_by(email=auth['email']).first()
    if not user:
        return make_response(jsonify({'access_token': "",'message': 'User doesn\'t exist','status':404}) )

    # Checks if user is an admin
    if user.admin:
        return make_response(jsonify({'access_token': "",'message': 'Can not login with admin account!','status':401}))

    # Checks if user is blocked
    if user.blocked:
        return make_response(jsonify({'access_token': "",'message': 'Unknown Error, try again!','status':400}))

    # Checks if password is correct
    if check_password_hash(user.password, auth['password']):
        # If correct returns makes and returns token
        token = jwt.encode(
            {'email': user.email, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=45)},
            current_app.config['SECRET_KEY'], "HS256")

        return make_response(jsonify({'access_token': token,'message':'logged in','status': 200}))

    return make_response(jsonify({'access_token': "",'message': 'Unknown Error, try again!','status':400}))

#get logged in user
@routes_blueprint.route('/users/me', methods=['GET'])
@token_required
def get_me(current_user):
    resp = make_response(jsonify({'data': Utilizador.serialize(current_user)}), 200)  # here you could use make_response(render_template(...)) too
    resp.headers['Access-Control-Allow-Origin'] = '*'
    return resp

#gets all users

@routes_blueprint.route('/users', methods=['GET'])
@admin_required
def get_all_users(current_user):
    #Query for all users
    result = []
    users = db.session.query(Utilizador).filter_by(deleted_at=None)

    for user in users:
        user_data = {}
        user_data['id'] = user.id
        user_data['username'] = user.username
        user_data['name'] = user.name
        user_data['email'] = user.email
        user_data['admin'] = user.admin
        user_data['blocked'] = user.blocked
        result.append(user_data)


    return make_response(jsonify({'data': result}), 200)

#get user by id
@routes_blueprint.route('/users/<user_id>', methods=['GET'])
@admin_required
def get_user(current_user,user_id):
    #find user
    user = db.session.query(Utilizador).filter_by(id=user_id,deleted_at=None).first()
    if not user:
        return make_response(jsonify({'message': "User doesnt exist"}), 404)

    return make_response(jsonify({'data': Utilizador.serialize(user)}), 200)

#update user
@routes_blueprint.route('/users/<user_id>', methods=['PUT'])
@admin_required
def update_user(current_user,user_id):
    #Checks if user exist
    user = db.session.query(Utilizador).filter_by(id=user_id,deleted_at=None).first()
    print(user_id)
    print(user)
    if not user:
        return make_response('User doesn\'t exist', 404)

    user_data = request.get_json()

    user.name = user_data['name']
    user.username = user_data['username']
    user.email = user_data['email']
    db.session.commit()
    return make_response(jsonify({'data': Utilizador.serialize(user)}), 200)
# endregion

# region Atividade
@routes_blueprint.route('/atividades', methods=['POST'])
@token_required
def create_atividade(current_user):
    data = request.get_json()

    new_atividade = Atividade(eventoID=data['eventoID'], userID=current_user.username, distanciaPercorrida=0,passos=0, dataInicio=datetime.datetime.utcnow(), dataFim=None,tipoAtividade=data['tipoAtividade'])
    db.session.add(new_atividade)
    db.session.commit()
    return make_response(jsonify("message': 'new atividade created'"), 200)


@routes_blueprint.route('/atividades', methods=['GET'])
@token_required
def get_atividades(current_user):
    atividades = db.session.query(Atividade).filter_by(userID=current_user.username).all()
    output = []
    for ati in atividades:
        atividade_data = {}
        atividade_data['id'] = ati.id
        atividade_data['eventoID'] = ati.eventoID
        atividade_data['userID'] = ati.userID
        atividade_data['distanciaPercorrida'] = ati.distanciaPercorrida
        atividade_data['passos'] = ati.passos
        atividade_data['dataInicio'] = ati.dataInicio
        atividade_data['dataFim'] = ati.dataFim
        atividade_data['tipoAtividade'] = ati.tipoAtividade
        output.append(atividade_data)

    return make_response(jsonify({'data': output}), 200)




@routes_blueprint.route('/atividades/<atividade_id>', methods=['PUT'])
@token_required
def update_atividade(current_user, atividade_id):
    atividade = db.session.query(Atividade).filter_by(id=atividade_id, userID=current_user.username).first()
    if not atividade:
        return make_response(jsonify({'message': 'atividade does not exist'}), 400)

    atividade_data = request.get_json()
    atividade.distanciaPercorrida = atividade_data['distanciaPercorrida']
    atividade.passos = atividade_data['passos']
    atividade.duracao = atividade_data['dataFim']
    atividade.tipoAtividade = atividade_data['tipoAtividade']

    db.session.commit()

    return make_response(jsonify({'message': 'atividade atualizada'}), 200)


# endregion

# region Evento
@routes_blueprint.route('/eventos', methods=['POST'])
@token_required
def create_evento(current_user):
    data = request.get_json()

    new_evento = Evento(nome=data['nome'], latitude=data['latitude'], longitude=data['longitude'],organizador=current_user.id,
                        estado=data['estado'], duracao=data['duracao'], descricao=data['descricao'],
                        acessibilidade=data['acessibilidade'], restricoes=data['restricoes'], tipoLixo=data['tipoLixo'],
                        volume=data['volume'], foto=data['foto'], observacoes=data['observacoes'],dataInicio=data['dataInicio'],
                        )
    db.session.add(new_evento)
    db.session.commit()
    return make_response(jsonify({'message': 'new evento created'}), 200)




@routes_blueprint.route('/eventos', methods=['GET'])
@token_required
def get_eventos(current_user):
    #todo order by data
    eventos = db.session.query(Evento).all()
    output = []
    for evento in eventos:
        evento_data = {}
        evento_data['id'] = evento.id
        evento_data['nome'] = evento.nome
        evento_data['organizador'] = evento.organizador
        evento_data['latitude'] = evento.nome
        evento_data['longitude'] = evento.nome
        evento_data['estado'] = evento.estado
        evento_data['duracao'] = evento.duracao
        evento_data['dataInicio'] = evento.dataInicio
        evento_data['descricao'] = evento.descricao
        evento_data['acessibilidade'] = evento.acessibilidade
        evento_data['restricoes'] = evento.restricoes
        evento_data['tipoLixo'] = evento.tipoLixo
        evento_data['volume'] = evento.volume
        evento_data['foto'] = evento.foto
        evento_data['observacoes'] = evento.observacoes
        evento_data['lixeiras'] = []
        for lix in  db.session.query(LixeiraEvento).filter_by(eventoID=evento.id):
            lixSer = LixeiraEvento.serialize(lix)
            evento_data['lixeiras'].append(lixSer)
        output.append(evento_data)




    return make_response(jsonify({'data': output}), 200)



@routes_blueprint.route('/eventos/<evento_id>', methods=['PUT'])
@token_required
def update_evento(current_user, evento_id):
    evento = db.session.query(Evento).filter_by(id=evento_id, organizador=current_user.username).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 400)

    evento_data = request.get_json()
    evento.duracao = evento_data['duracao']
    evento.dataInicio = evento_data['dataInicio']
    evento.descricao = evento_data['descricao']
    evento.acessibilidade = evento_data['acessibilidade']
    evento.restricoes = evento_data['restricoes']
    evento.tipoLixo = evento_data['tipoLixo']
    evento.volume = evento_data['volume']
    evento.foto = evento_data['foto']
    evento.observacoes = evento_data['observacoes']

    db.session.commit()
    return make_response(jsonify({'message': 'evento atualizada'}), 200)





@routes_blueprint.route('/eventos/<evento_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_evento(evento_id):
    evento = db.session.query(Evento).filter_by(id=evento_id).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 400)

    evento_data = request.get_json()

    evento.estado = evento_data['estado']

    db.session.commit()
    return make_response(jsonify({'message': 'evento atualizada'}), 200)


# endregion

#region Lixeira
@routes_blueprint.route('/lixeiras', methods=['POST'])
@guest
def create_lixeira(current_user):
    data = request.get_json()
    print(data)
    # Checks if lixeira with same coordinates exists
    lix = db.session.query(Lixeira).filter_by(latitude=data['latitude']).first()
    if lix:
          if lix.longitude == float(data['longitude']) :
            return make_response(jsonify({'message': 'Lixeira already exists! Update it instead!','status': 409}))


    new_lixeira = Lixeira(nome=data['nome'],latitude=data['latitude'],longitude=data['longitude'], criador=current_user.id,
                        estado=data['estado'], aprovado=data['aprovado'], foto=data['foto'])
    db.session.add(new_lixeira)
    db.session.commit()
    return make_response(jsonify({'message': 'local lixo criado','status':  200}))



@routes_blueprint.route('/lixeiras', methods=['GET'])
def get_all_lixeiras():
    lixeiras = db.session.query(Lixeira).all()
    output = []
    for lixeira in lixeiras:
        lixeira_data = {}
        lixeira_data['id'] = lixeira.id
        lixeira_data['nome'] = lixeira.nome
        lixeira_data['latitude'] = lixeira.latitude
        lixeira_data['longitude'] = lixeira.longitude
        lixeira_data['criador'] = lixeira.criador
        lixeira_data['estado'] = lixeira.estado
        lixeira_data['aprovado'] = lixeira.aprovado
        lixeira_data['foto'] = lixeira.foto
        lixeira_data['eventos'] = []
        for ev in db.session.query(LixeiraEvento).filter_by(lixeiraID=lixeira.id):
            evSer = LixeiraEvento.serialize(ev)
            lixeira_data['eventos'].append(evSer)
        output.append(lixeira_data)
    if len(output) == 0:
        return make_response(jsonify({'data':[],'staus':404,'message':'no locais lixo'}))

    return make_response(jsonify({'data': output,'status':200,'message':'locais lixo encontrados'}))

@routes_blueprint.route('/lixeiras/<lixeira_id>', methods=['GET'])
def get_lixeira(current_user,lixeira_id):
    lixeira = db.session.query(Lixeira).filter_by(id=lixeira_id).first()

    return make_response(jsonify({'data': Lixeira.serialize(lixeira),'status':200}))

@routes_blueprint.route('/lixeiras/<lixeira_id>', methods=['PUT'])
@token_required
def update_lixeira(current_user, lixeira_id):
    lixeira = db.session.query(Lixeira).filter_by(id=lixeira_id, criador=current_user.id).first()
    if not evento:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)

    lixeira_data = request.get_json()
    lixeira.estado = lixeira_data['estado']
    evento.foto = evento_data['foto']

    db.session.commit()
    return make_response(jsonify({'message': 'lixeira atualizada'}), 200)




@routes_blueprint.route('/lixeiras/<lixeira_id>/aprovar', methods=['PATCH'])
@admin_required
def aprovar_lixeira(current_user,lixeira_id):
    lixeira = db.session.query(Lixeira).filter_by(id=lixeira_id).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)
    lixeira_data = request.get_json()

    lixeira.aprovado = lixeira_data['aprovado']
    if lixeira_data['aprovado'] == 'false':
        lixeira.aprovado = False
    elif lixeira_data['aprovado'] == 'true':
        lixeira.aprovado = True


    db.session.commit()
    return make_response(jsonify({'message': 'lixeira atualizada'}), 200)


@routes_blueprint.route('/lixeiras/<lixeira_id>/mudarEstadoLixeira', methods=['PATCH'])
@token_required
def mudar_estado_lixeira(current_user,lixeira_id):
    lixeira = db.session.query(Lixeira).filter_by(id=lixeira_id).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)
    lixeira_data = request.get_json()

    lixeira.estado = lixeira_data['estado']
    lixeira_data['estado'] = lixeira.estado

    db.session.commit()
    return make_response(jsonify({'message': 'estado do local de lixo atualizado'}), 200)

#endregion

#region evento-lixeira
@routes_blueprint.route('/eventos/<evento_id>/addLixeira', methods=['POST'])
@token_required
def add_lixeira_to_event(current_user,evento_id):
    evento = db.session.query(Evento).filter_by(id=evento_id).first()
    if not evento:
        return make_response(jsonify({'message': 'evento does not exist'}), 404)

    data = request.get_json()
    lixeira = db.session.query(Lixeira).filter_by(id=data["lixeiraID"]).first()
    if not lixeira:
        return make_response(jsonify({'message': 'lixeira does not exist'}), 404)


    inscricao = LixeiraEvento(lixeiraID=data['lixeiraID'],eventoID=evento_id)

    db.session.add(inscricao)
    db.session.commit()
    return make_response(jsonify({'message': 'lixeira adicionada ao evento'}), 200)

@routes_blueprint.route('/eventos/<evento_id>/lixeiras', methods=['GET'])
@token_required
def get_lixeiras_no_evento(current_user,evento_id):
    lixeirasEvento = db.session.query(LixeiraEvento).filter_by(eventoID=evento_id)
    result = []
    for lix in lixeirasEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventoID'] = evento_id
        lix_data['lixeiraID'] = lix.lixeiraID
        result.append(lix_data)

    return make_response(jsonify({'data': result}), 200)


@routes_blueprint.route('/lixeiras/<lixeira_id>/eventos', methods=['GET'])
@token_required
def get_eventos_na_lixiera(current_user, lixeira_id):
    lixeirasEvento = db.session.query(LixeiraEvento).filter_by(lixeiraID=lixeira_id)
    result = []
    for lix in lixeirasEvento:
        lix_data = {}
        lix_data['id'] = lix.id
        lix_data['eventoID'] = evento_id
        lix_data['lixeiraID'] = lix.lixeiraID
        result.append(lix_data)

    return make_response(jsonify({'data': result}), 200)
#endregion

