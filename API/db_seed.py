# IMPORTS FOR DB ACCESS/MANIPULATION
import datetime
from models import User, GarbageSpot, Activity, Event, Message, Garbage, GarbageInActivity, GarbageType, ActivityType
from models import Equipment, EquipmentInEvent, IndividualMessage, EventMessage
from models import UserInEvent, GarbageSpotInEvent
from werkzeug.security import generate_password_hash
from sqlalchemy import create_engine, desc
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
    userDefault1 = User(username="user", password=generate_password_hash("123"), name="Nome", email="user@mail.pt",
                        admin=False, blocked=False)

    userDefault2 = User(username="user2", password=generate_password_hash("123"), name="Nome2", email="user2@mail.pt",
                        admin=False, blocked=False)
    session.add(userDefault1)
    session.add(userDefault2)
    session.commit()

    adminDefault = User(username="admin", password=generate_password_hash("123"), name="Nome",
                        email="admin@mail.pt", admin=True, blocked=False)
    session.add(adminDefault)
    session.commit()

    # SEED UTILIZADORES
    for i in range(10):
        first_name = names.get_first_name()
        last_name = names.get_last_name()
        full_name = first_name + ' ' + last_name

        number = randrange(10)
        uname = '{}{}{:03d}'.format(first_name[0].lower(),
                                    last_name[:3].lower(),
                                    number)
        email = uname + "@mail.pt"
        session.add(
            User(username=uname, password=generate_password_hash("123"), name=full_name, email=email, admin=False,
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
            User(username=uname, password=generate_password_hash("123"), name=full_name, email=email, admin=True,
                 blocked=False))

    print("---ADMINS seed done!")
    # session.close()

    # SEED LIXEIRA
    session.query(GarbageSpot).delete()
    for i in range(10):
        name = str(r.get_random_word())
        latitude = round(random.uniform(38.779875, 41.575756), 5)
        longitude = round(random.uniform(-8.199258, -7.886036), 5)
        creator = session.query(User).filter_by(admin=False).order_by(func.random()).first()
        status = ["Muito sujo", "Pouco sujo", "Limpo"]
        garbageSpot = GarbageSpot(name=name, latitude=latitude, longitude=longitude, creator=creator.id,
                                  status=random.choice(status), approved=True, createdDate=datetime.datetime.utcnow())

        session.add(garbageSpot)
        session.flush()

        today = date.today()
        # ddmmYY
        # garbageSpot.foto = str(garbageSpot.id) + creator.username + str(today.strftime("%d%m%Y")) + '.png'
        # todo create a foto ???

    print("---GarbageSpot seed done!")

    # SEED Garbage
    session.query(Garbage).delete()
    for i in range(6):
        name = ["Mascara", "Garrafa", "Saco", "Plastico", "Cartao", "Vidro", "Outro"]
        garbage = Garbage(name=name[i])
        session.add(garbage)
    print("---Garbage seed done!")

    # SEED GarbageType
    session.query(GarbageType).delete()
    for i in range(3):
        name = ["Industrial", "Doméstico", "Outro"]
        garbageType = GarbageType(name=name[i])
        session.add(garbageType)

    print("---Garbage Type seed done!")

    # SEED ActivityType
    session.query(ActivityType).delete()
    for i in range(3):
        name = ["Walk", "Run", "Bike"]
        actiType = ActivityType(name=name[i])
        session.add(actiType)

    print("---Activity Type seed done!")

    # SEED EVENTO
    session.query(Event).delete()
    for i in range(10):
        latitude = round(random.uniform(38.779875, 41.575756), 5)
        longitude = round(random.uniform(-8.199258, -7.886036), 5)
        # organizer = session.query(User).filter_by(admin=False).order_by(func.random()).first()

        status = ["Criado", "Começado", "Cancelado", "Finalizado"]
        accessibility = ["Reduzida", "Suficiente"]
        quantity = ["Muita", "Pouca", "Media"]
        restrictions = ["Todas as idades", "Não indicado para crianças"]
        garbageType = []
        # for a in range(2):
        #    garbage = session.query(Garbage).order_by(func.random()).first()
        #    if garbage.id not in garbageType:
        #        garbageType.append(garbage.id)
        # print(garbage.name)

        # to json, so we can insert array in database (sqlite doesnt support arrays in database)
        # garbageTypeJSON = json.dumps(garbageType)
        # 72 hours ->
        duration = randrange(127)

        name = str(r.get_random_word()) + " " + str(r.get_random_word())
        description = ""
        observations = ""
        for b in range(2):
            description = str(description) + " " + str(r.get_random_word())
            observations = f"{observations} {r.get_random_word()}"

        event = Event(name=name, description=description, observations=observations, latitude=latitude,
                      longitude=longitude,
                      status=random.choice(status), accessibility=random.choice(accessibility),
                      quantity=random.choice(quantity), restrictions=random.choice(restrictions),
                      duration=duration, startDate=datetime.datetime.utcnow(), createdDate=datetime.datetime.utcnow())

        session.add(event)

        today = date.today()
        # ddmmYY
        # event.foto = str(event.id) + str(organizer.id) + str(today.strftime("%d%m%Y")) + '.png'
        # todo create a foto ???

        session.commit()
    print("---Event seed done!")

    # SEED ATIVIDADE
    session.query(Activity).delete()
    for i in range(5):
        userID = session.query(User).filter_by(admin=False).order_by(func.random()).first()
        eventID = session.query(Event).order_by(func.random()).first()
        distanceTravelled = randrange(99999)
        steps = randrange(99999)
        startDate = datetime.datetime.utcnow()
        td = timedelta(days=randrange(3))
        # your calculated date
        my_date = startDate + td
        endDate = [None, my_date]

        activityType = session.query(ActivityType).order_by(func.random()).first()

        activity = Activity(userID=userID.id, eventID=eventID.id, distanceTravelled=distanceTravelled, steps=steps,
                            startDate=startDate, endDate=random.choice(endDate), activityTypeID=activityType.id)
        session.add(activity)

    print("---Activity seed done!")

    # SEED LIXONAATIVIDADE
    session.query(GarbageInActivity).delete()

    for i in range(5):
        activityID = session.query(Activity).order_by(func.random()).first()
        garbageID = session.query(Garbage).order_by(func.random()).first()
        amount = randrange(100)
        unitType = ["kgs", "litros", "unidades"]

        garbageInActivity = GarbageInActivity(activityID=activityID.id, garbageID=garbageID.id, amount=amount,
                                              unitType=random.choice(unitType))
        session.add(garbageInActivity)

    print("---GarbageInActivity seed done!")

    # SEED EQUIPAMENTO
    session.query(Equipment).delete()
    for i in range(6):
        name = ["Saco de Garbage", "Luvas", "Pá", "Tesoura", "Faca", "Contentor"]
        eq = Equipment(name=name[i])
        session.add(eq)

    print("---Equipment seed done!")

    # SEED EQUIPAMENTONOEVENTO
    session.query(EquipmentInEvent).delete()
    for i in range(6):

        equipment = session.query(Equipment).order_by(func.random()).first()
        event = session.query(Event).order_by(func.random()).first()
        observations = ""
        for b in range(2):
            observations = str(observations) + " " + str(r.get_random_word())

        isProvided = bool(random.getrandbits(1))

        eq = EquipmentInEvent(equipmentID=equipment.id, eventID=event.id, observations=observations,
                              isProvided=isProvided)
        session.add(eq)

    print("---EquipmentInEvent seed done!")

    # SEED LIXEIRAEVENTO
    session.query(GarbageSpotInEvent).delete()
    for i in range(6):
        garbageSpot = session.query(GarbageSpot).order_by(func.random()).first()
        event = session.query(Event).order_by(func.random()).first()

        garbageSpotInEvent = GarbageSpotInEvent(garbageSpotID=garbageSpot.id, eventID=event.id)
        session.add(garbageSpotInEvent)

    print("---GarbageSpotInEvent seed done!")

    # SEED UTILIZADORNOEVENTO

    session.query(UserInEvent).delete()

    for i in range(3):
        event = session.query(Event).order_by(func.random()).first()
        # status2 = ["Confirmado", "Não Confirmado", "Inscrito"]

        userInEvent = UserInEvent(userID=1, eventID=event.id, status="Organizer", creator=True, enteringDate=today)
        session.add(userInEvent)
    today = date.today()
    for i in range(6):
        user = session.query(User).filter_by(admin=False).order_by(func.random()).first()
        event = session.query(Event).order_by(func.random()).first()
        status2 = ["Confirmado", "Inscrito", "Não Confirmado"]

        userInEvent = UserInEvent(userID=user.id, eventID=event.id, status=random.choice(status2), creator=False, enteringDate=today)
        session.add(userInEvent)

    print("---UserInEvent seed done!")

    # SEED MESSAGES
    session.query(IndividualMessage).delete()
    session.query(EventMessage).delete()
    session.query(Message).delete()

    for i in range(3):
        message = Message(senderID=1, message="random message 1", status="Sent", type="Individual",
                          sentDate=datetime.datetime.utcnow())
        session.add(message)
        session.flush()
        messageInd = IndividualMessage(receiverID=2, messageID=message.id)

        session.add(messageInd)

    for i in range(3):
        message = Message(senderID=2, message="random message 2", status="Sent", type="Individual",
                          sentDate=datetime.datetime.utcnow())
        session.add(message)
        session.flush()
        messageInd = IndividualMessage(receiverID=1, messageID=message.id)

        session.add(messageInd)

    for i in range(3):
        message = Message(senderID=1, message="random message ev", status="Sent", type="Event",
                          sentDate=datetime.datetime.utcnow())

        session.add(message)
        session.flush()
        messageEve = EventMessage(eventID=2, messageID=message.id)

        session.add(messageEve)

    session.commit()
    print("---Message seed done!")
