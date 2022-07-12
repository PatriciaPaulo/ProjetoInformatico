from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import MetaData, Column, Integer, String, Boolean, ForeignKey, Numeric, Text, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine


metadata = MetaData()
Base = declarative_base(metadata=metadata)
engine = create_engine('sqlite:///spl.db')
db = SQLAlchemy(metadata=metadata)


# region User
class User(Base):
    __tablename__ = "user"
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String(50), nullable=False)
    name = Column(String(128))
    email = Column(String(128), nullable=False)
    admin = Column(Boolean, nullable=False, default=False)
    blocked = Column(Boolean, nullable=False, default=False)
    confirmed = Column(Boolean, nullable=False, default=False)
    deleted_at = Column(DateTime)

    def serialize(self):
        return {
            'id': self.id,
            'username': self.username,
            'name': self.name,
            'email': self.email,
            'admin': self.admin,
            'blocked': self.blocked,
            'confirmed': self.confirmed
        }


# region Activity
class Activity(Base):
    __tablename__ = "activity"
    id = Column(Integer, primary_key=True)
    eventID = Column(Integer, ForeignKey('event.id'), nullable=True)
    userID = Column(Integer, ForeignKey('user.id'), nullable=False)
    distanceTravelled = Column(String(50))
    steps = Column(String(50))
    activityType = Column(String(50))
    startDate = Column( DateTime)
    endDate = Column( DateTime)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'eventID': self.eventID,
            'distanceTravelled': self.distanceTravelled,
            'steps': self.steps,
            'startDate': self.startDate,
            'endDate': self.endDate,
            'activityType': self.activityType
        }
# endregion


# region GarbageInActivity
class GarbageInActivity(Base):
    __tablename__ = "garbage_in_activity"
    id = Column(Integer, primary_key=True)
    activityID = Column(Integer, ForeignKey('activity.id'), nullable=False)
    garbageID = Column(Integer, ForeignKey('garbage.id'), nullable=False)
    amount = Column(String(128), nullable=False)
    unitType = Column(String(128), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'activityID': self.activityID,
            'garbageID': self.garbageID,
            'amount': self.amount,
            'unitType': self.unitType
        }
# endregion


# region Garbage
class Garbage(Base):
    __tablename__ = "garbage"
    id = Column(Integer, primary_key=True)
    name = Column(String(50), unique=True, nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'name': self.name

        }
# endregion


# region Equipment
class Equipment(Base):
    __tablename__ = "equipment"
    id = Column(Integer, primary_key=True)
    name = Column(String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'name': self.name

        }
# endregion


# region EquipmentInEvent
class EquipmentInEvent(Base):
    __tablename__ = "equipment_in_event"
    id = Column(Integer, primary_key=True)
    equipmentID = Column(Integer, ForeignKey('equipment.id'), nullable=False)
    eventID = Column(Integer, ForeignKey('event.id'), nullable=False)
    observations = Column(String(50))
    isProvided = Column(Boolean)

    def serialize(self):
        return {
            'id': self.id,
            'equipmentID': self.equipmentID,
            'eventID': self.eventID,
            'observations': self.observations,
            'isProvided': self.isProvided

        }
# endregion

# region Event
class Event(Base):
    __tablename__ = "event"
    id = Column(Integer, primary_key=True)
    name = Column(String(128), nullable=False)
    latitude = Column(Numeric(128), nullable=False)
    longitude = Column(Numeric(128), nullable=False)
    status = Column(String(50))
    duration = Column(String(50))
    startDate = Column( DateTime)
    description = Column(String(50))
    accessibility = Column(String(50))
    restrictions = Column(String(50))
    garbageType = Column(String(50))
    quantity = Column(String(50))
    observations = Column(String(50))

    def serialize(self):
        return {
            'id': self.id,
            'name': self.name,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'status': self.status,
            'duration': self.duration,
            'startDate': self.startDate,
            'description': self.description,
            'accessibility': self.accessibility,
            'restrictions': self.restrictions,
            'garbageType': self.garbageType,
            'quantity': self.quantity,
            'observations': self.observations
        }
# endregion


# region GarbageSpot
class GarbageSpot(Base):
    __tablename__ = "garbage_spot"
    id = Column(Integer, primary_key=True)
    name = Column(String(50), nullable=False)
    latitude = Column(Integer, nullable=False)
    longitude = Column(Integer, nullable=False)
    creator = Column(Integer, ForeignKey('user.id'), nullable=False)
    status = Column(String(50), nullable=False)
    approved = Column(Boolean, nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'name': self.name,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'creator': self.creator,
            'status': self.status,
            'approved': self.approved,
        }

# endregion


# region GarbageSpotInEvent
class GarbageSpotInEvent(Base):
    __tablename__ = "garbagespot_in_event"
    id = Column(Integer, primary_key=True)
    garbageSpotID = Column(Integer, ForeignKey('garbage_spot.id'), nullable=False)
    eventID = Column(Integer, ForeignKey('event.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'garbageSpotID': self.garbageSpotID,
            'eventID': self.eventID

        }
# endregion


# region UserInEvent
class UserInEvent(Base):
    __tablename__ = "user_in_event"
    id = Column(Integer, primary_key=True)
    userID = Column(Integer, ForeignKey('user.id'), nullable=False)
    eventID = Column(Integer, ForeignKey('event.id'), nullable=False)
    status = Column(String(128), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'eventID': self.eventID,
            'status': self.status
        }
# endregion


# region MessageInEvent
class MessageInEvent(Base):
    __tablename__ = "message_in_event"
    id = Column(Integer, primary_key=True)
    messageID = Column(Integer, ForeignKey('message.id'), nullable=False)
    eventID = Column(Integer, ForeignKey('event.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'messageID': self.messageID,
            'eventID': self.eventID

        }
# endregion


# region Friendship
class Friendship(Base):
    __tablename__ = "friendship"
    id = Column(Integer, primary_key=True)
    requestorID = Column(Integer, ForeignKey('user.id'), nullable=False)
    addresseeID = Column(Integer, ForeignKey('user.id'), nullable=False)
    date = Column(String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'requestorID': self.requestorID,
            'addresseeID': self.addresseeID,
            'date': self.date
        }
# endregion


# region Message
class Message(Base):
    __tablename__ = "message"
    id = Column(Integer, primary_key=True)
    userID = Column(Integer, ForeignKey('user.id'), nullable=False)
    message = Column(String(50), nullable=False)
    type = Column(String(50), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'message': self.message,
            'type': self.type
        }
# endregion


# region IndividualMessage
class IndividualMessage(Base):
    __tablename__ = "individual_message"
    id = Column(Integer, primary_key=True)
    userID = Column(Integer, ForeignKey('user.id'), nullable=False)
    messageID = Column(Integer, ForeignKey('message.id'), nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'userID': self.userID,
            'messageID': self.messageID
        }
# endregion
