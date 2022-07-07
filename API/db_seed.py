# IMPORTS FOR DB ACCESS/MANIPULATION
import datetime
from models import Utilizador,Lixeira,Atividade,Evento,Mensagem,Lixo,LixoNaAtividade
from models import Equipamento, EquipamentoNoEvento,MensagemIndividual,MensagemEvento
from models import UtilizadorNoEvento,LixeiraEvento
from werkzeug.security import generate_password_hash
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.sql.expression import func

# IMPORTS FOR VALUES
import names
from datetime import date, timedelta
from random import randrange
from random_word import RandomWords
import random
import json


if __name__ == '__main__':
    engine = create_engine('sqlite:///spl.db')
    session = Session(engine)
    r = RandomWords()

    # CREATE DEFAULTS
    userDefault = Utilizador(username="user", password=generate_password_hash("123"), name="Nome", email="user@mail.pt",
                             admin=False, blocked=False)
    session.add(userDefault)
    session.commit()

    adminDefault = Utilizador(username="admin", password=generate_password_hash("123"), name="Nome",
                              email="admin@mail.pt", admin=True, blocked=False)
    session.add(adminDefault)
    session.commit()

    # SEED UTILIZADORES
    for i in range(10):
        first_name = names.get_first_name()
        last_name = names.get_last_name()
        full_name = first_name + ' ' + last_name

        number =randrange(10)
        uname = '{}{}{:03d}'.format(first_name[0].lower(),
                                       last_name[:3].lower(),
                                       number)
        email = uname + "@mail.pt"
        session.add(
            Utilizador(username=uname, password=generate_password_hash("123"), name=full_name, email=email, admin=False,
                       blocked=False, confirmed=True, deleted_at=None))
        session.commit()

    print("---UTILIZADORES seed done!")

    # SEED ADMINS
    for i in range(5):
        first_name = names.get_first_name()
        last_name = names.get_last_name()
        full_name = first_name + ' ' + last_name

        number = randrange(10)
        uname = '{}{}{:03d}'.format(first_name[0].lower(),
                                    last_name[:3].lower(),
                                    number)
        email = uname + "@mail.pt"
        session.add(
            Utilizador(username=uname, password=generate_password_hash("123"), name=full_name, email=email, admin=True,
                       blocked=False))
        session.commit()
    print("---ADMINS seed done!")
    # session.close()
    
    #SEED LIXEIRA
    session.query(Lixeira).delete()
    for i in range(10):
        nome = str(r.get_random_word())
        latitude = round(random.uniform(38.779875, 41.575756), 5)
        longitude = round(random.uniform(-8.199258, -7.886036), 5)
        criador = session.query(Utilizador).filter_by(admin=False).order_by(func.random()).first()
        estado = ["Muito sujo", "Pouco sujo", "Limpo"]
        lixeira = Lixeira(nome=nome,latitude=latitude,longitude=longitude,criador=criador.id,estado=random.choice(estado),aprovado=True,foto="foto")

        session.add(lixeira)
        session.flush()


        today = date.today()
        # ddmmYY
        lixeira.foto = str(lixeira.id) + criador.username + str(today.strftime("%d%m%Y")) + '.png'
        #todo create a foto ???

        session.commit()
    print("---Lixeira seed done!")

    # SEED Lixo
    session.query(Lixo).delete()
    for i in range(6):
        nome = ["Mascara", "Garrafa", "Saco","Plastico","Cartao","Vidro"]
        lixo = Lixo(nome=nome[i])
        session.add(lixo)
        session.commit()

    print("---Lixo seed done!")
    # SEED EVENTO
    session.query(Evento).delete()  
    for i in range(5):
        latitude = round(random.uniform(38.779875,41.575756), 5)
        longitude = round(random.uniform(-8.199258, -7.886036), 5)
        organizador = session.query(Utilizador).filter_by(admin=False).order_by(func.random()).first()

        estado = ["Criado", "Começado", "Cancelado","Finalizado"]
        acessibilidade = ["Reduzida", "Suficiente"]
        volume = ["Muito", "Pouco","Medio"]
        restricoes = ["Todas as idades", "Não indicado para crianças"]
        tipoLixo = []
        for a in range(2):
            lixo = session.query(Lixo).order_by(func.random()).first()
            if lixo.id not in tipoLixo:
                tipoLixo.append(lixo.id)
                #print(lixo.nome)

        #to json, so we can insert array in database (sqlite doesnt support arrays in database)
        tipoLixoJSON = json.dumps(tipoLixo)
        # 72 hours ->
        duracao = randrange(4320)

        nome =  str(r.get_random_word()) + " "+ str(r.get_random_word())
        descricao = ""
        observacoes = ""
        for b in range(2):
            descricao = str(descricao)+ " " + str(r.get_random_word())
            observacoes = str(observacoes)+ " " + str(r.get_random_word())


        evento = Evento(nome=nome,descricao=descricao,observacoes=observacoes,latitude=latitude,
                        longitude=longitude,organizador=organizador.id,
                        estado=random.choice(estado),acessibilidade=random.choice(acessibilidade),
                        volume=random.choice(volume),restricoes=random.choice(restricoes),
                        tipoLixo=tipoLixoJSON,duracao=duracao,dataInicio=datetime.datetime.utcnow())
        session.add(evento)


        today = date.today()
        # ddmmYY
        evento.foto = str(evento.id) + str(organizador.id) + str(today.strftime("%d%m%Y")) + '.png'
        # todo create a foto ???

        session.commit()
    print("---Evento seed done!")

    # SEED ATIVIDADE
    session.query(Atividade).delete()
    for i in range(5):
        userID = session.query(Utilizador).filter_by(admin=False).order_by(func.random()).first()
        eventoID = session.query(Evento).order_by(func.random()).first()
        distanciaPercorrida = randrange(99999)
        passos = randrange(99999)
        dataInicio =  datetime.datetime.utcnow()
        td = timedelta(days=randrange(3))
        # your calculated date
        my_date = dataInicio + td
        dataFim = [None,my_date]


        tipoAtividade = ["corrida","caminhada","bicicleta"]

        atividade = Atividade(userID=userID.id, eventoID=eventoID.id, distanciaPercorrida=distanciaPercorrida, passos=passos,
                    dataInicio=dataInicio, dataFim=random.choice(dataFim), tipoAtividade=random.choice(tipoAtividade))
        session.add(atividade)

        session.commit()
    print("---Atividade seed done!")


    # SEED LIXONAATIVIDADE
    session.query(LixoNaAtividade).delete()

    for i in range(5):
        atividadeID = session.query(Atividade).order_by(func.random()).first()
        lixoID = session.query(Lixo).order_by(func.random()).first()
        quantidade = randrange(100)
        medida = ["kgs","litros","unidades"]


        lixonaatividade = LixoNaAtividade(atividadeID=atividadeID.id,lixoID=lixoID.id,quantidade=quantidade,medida=random.choice(medida))
        session.add(lixonaatividade)

        session.commit()
    print("---LixoNaAtividade seed done!")

    # SEED EQUIPAMENTO
    session.query(Equipamento).delete()
    for i in range(6):
        nome = ["Saco de Lixo", "Luvas", "Pá","Tesoura","Faca","Contentor"]
        eq = Equipamento(nome=nome[i])
        session.add(eq)
        session.commit()

    print("---Equipamento seed done!")

    # SEED EQUIPAMENTONOEVENTO
    session.query(EquipamentoNoEvento).delete()
    for i in range(6):

        equipamento = session.query(Equipamento).order_by(func.random()).first()
        evento = session.query(Evento).order_by(func.random()).first()
        observacoes=""
        for b in range(2):
            observacoes = str(observacoes) + " " + str(r.get_random_word())


        isProvided = bool(random.getrandbits(1))

        eq = EquipamentoNoEvento(equipamentoID=equipamento.id,eventoID=evento.id,observacoes=observacoes,isProvided=isProvided)
        session.add(eq)
        session.commit()

    print("---EquipamentoNoEvento seed done!")

    # SEED LIXEIRAEVENTO
    session.query(LixeiraEvento).delete()
    for i in range(6):

        lixeira = session.query(Lixeira).order_by(func.random()).first()
        evento = session.query(Evento).order_by(func.random()).first()

        lixEv = LixeiraEvento(lixeiraID=lixeira.id, eventoID=evento.id)
        session.add(lixEv)
        session.commit()
    print("---LixeiraEvento seed done!")

    # SEED UTILIZADORNOEVENTO

    session.query(UtilizadorNoEvento).delete()
    for i in range(6):

        utilizador = session.query(Utilizador).filter_by(admin=False).order_by(func.random()).first()
        evento = session.query(Evento).order_by(func.random()).first()
        estado = ["Confirmado","Inscrito","Cancelado"]

        utilizadorNoEvento = UtilizadorNoEvento(userID=Utilizador.id, eventoID=evento.id,estado=random.choice(estado))
        session.add(lixEv)
        session.commit()

    print("---UtilizadorNoEvento seed done!")
    
