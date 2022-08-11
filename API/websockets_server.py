import asyncio
import datetime
import json
from threading import Thread

import jwt
from flask import jsonify
from sqlalchemy import create_engine
from sqlalchemy.orm import Session

import websockets
from models import User, Message, IndividualMessage, EventMessage, UserInEvent, Event

SECRET_KEY = '6e129cb9707e18357de8b945656c430f'
engine = create_engine('sqlite:///spl.db')
users_connected = dict()
users_event_channel = dict()
websocket_listen_event_loop = asyncio.new_event_loop()

asyncio.set_event_loop(websocket_listen_event_loop)


async def handler(websocket):
    session = Session(engine)

    token = websocket.request_headers["token"]
    data = jwt.decode(token.split(" ")[1], SECRET_KEY, algorithms=["HS256"])
    current_user = session.query(User).filter_by(email=data['email']).first()

    # save connected user
    users_connected[current_user.id] = websocket
    # save events user
    events = session.query(Event).all()
    for event in events:
        if event.status is not "Finalizado":
           users_event_channel[event.id] = []


    eventsuser = session.query(UserInEvent).filter_by(userID=current_user.id).all()
    for eventuser in eventsuser:
            if current_user not in users_event_channel[eventuser.eventID]:
                users_event_channel[eventuser.eventID].append(current_user.id)


    # save user in event

    """
    #check if theres messages not received
    individualMessages = session.query(IndividualMessage).filter_by(receiverID=current_user.id).all()


    for individualMessage in individualMessages:
        message = session.query(Message).filter_by(id=individualMessage.messageID).first()
        if message.status == "Sent":
            send_notification(current_user.id, message)


    messages = session.query(EventMessage).filter_by(receiverID=current_user.id).all()

    for eveMessage in messages:
        message = session.query(Message).filter_by(id=eveMessage.messageID).first()
        if message.status == "Sent":
            send_notification(current_user.id, message)

"""
    print(f"{current_user.username} connected")
    while True:
        messageID = await websocket.recv()

        print(messageID)
        message = session.query(Message).filter_by(id=messageID).first()

        if message:
            message.status = "Received"
            messageInd = session.query(IndividualMessage).filter_by(messageID=messageID).first()
            if messageInd:
                messageInd.deliveryDate = datetime.datetime.now()

            messageEv = session.query(EventMessage).filter_by(messageID=messageID).first()
            if messageEv:
                messageEv.deliveryDate = datetime.datetime.now()

        session.commit()


async def main():
    print('start_websockets')
    async with websockets.serve(handler, "", 5001):
        await asyncio.Future()  # run forever


def _run_websockets():
    global websocket_listen_event_loop
    websocket_listen_event_loop.run_until_complete(main())
    websocket_listen_event_loop.run_forever()


def start_websockets():
    websockets_thread = Thread(target=_run_websockets, daemon=True)
    websockets_thread.start()


def send_notification_user(userID, message):
    if userID not in users_connected:
        return
    data_to_send = {"message": message.serialize()}
    websocket_listen_event_loop.create_task(users_connected[userID].send(json.dumps(data_to_send)))

def send_notification_event(eventID,message,current_userID):
    if eventID not in users_event_channel:
        return
    for user in users_event_channel[eventID]:
        if not user == current_userID:
            data_to_send = {"message": message.serialize(), "eventID": eventID}

            websocket_listen_event_loop.create_task(users_connected[user].send(json.dumps(data_to_send)))



