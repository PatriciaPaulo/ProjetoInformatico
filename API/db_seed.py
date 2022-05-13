#IMPORTS FOR DB ACCESS/MANIPULATION
from models import Utilizador,Lixeira,Atividade,Evento,Mensagem,Lixo,LixoNaAtividade
from models import Equipamento, EquipamentoNoEvento,MensagemIndividual,MensagemEvento
from werkzeug.security import generate_password_hash
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from  sqlalchemy.sql.expression import func

#IMPORTS FOR VALUES
import names
from datetime import date
from random import randrange
from random_word import RandomWords
import random
import json


if __name__ == '__main__':
    engine = create_engine('sqlite:///spl.db')
    session = Session(engine)
    r = RandomWords()

    # SEED UTILIZADORES
    session.query(Utilizador).delete()
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
                       blocked=False,deleted_at=None))
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
        from datetime import date

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
                        tipoLixo=tipoLixoJSON,duracao=duracao)
        session.add(evento)
        from datetime import date

        today = date.today()
        # ddmmYY
        evento.foto = str(evento.id) + str(organizador.id) + str(today.strftime("%d%m%Y")) + '.png'
        # todo create a foto ???

        session.commit()
    print("---Evento seed done!")
    """
     
    # SEED LIXONAATIVIDADE
    session.query(LixoNaAtividade).delete()
    for i in range(10):
        organizador = session.query(Utilizador).filter_by(admin=False).order_by(func.random()).first()
        organizador = session.query(Utilizador).filter_by(admin=False).order_by(func.random()).first()

        atividadeID = Column(Integer, ForeignKey('atividade.id'), nullable=False)
            lixoID = Column(Integer, ForeignKey('lixo.id'), nullable=False)
            quantidade = Column(String(128), nullable=False)
            medida = Column(String(128), nullable=False)

       
     
        acessibilidade = ["Reduzida", "Suficiente"]
        volume = ["Muito", "Pouco","Medio"]
    
     

        evento = Evento(nome=nome,descricao=descricao,observacoes=observacoes,latitude=latitude,
                        longitude=longitude,organizador=organizador,estado=estado,acessibilidade=acessibilidade,
                        volume=volume,restricoes=restricoes,tipoLixo=tipoLixo,duracao=duracao)
        session.add(evento)
        from datetime import date

        today = date.today()
        # ddmmYY
        evento.foto = str(evento.id) + criador.username + str(today.strftime("%d%m%Y")) + '.png'
        # todo create a foto ???

        session.commit() """