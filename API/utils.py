from functools import wraps
import re
import jwt
from flask import Flask, jsonify, request, make_response,current_app
from models import User, db


class Guest(object):
    id = ""


def guest(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        if 'authorization' in request.headers:
            token = request.headers['authorization']

            try:
                if token == "0":
                    current_user = Guest()
                    current_user.id = 0
                else:
                    data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
                    current_user = db.session.query(User).filter_by(email=data['email']).first()

            except Exception as ex:
                print(ex)
                return make_response(jsonify({'message': 'token is invalid'}), 400)

        else:
            return make_response(jsonify({'message': 'token not found'}), 403)
        return f(current_user, *args, **kwargs)

    return decorator


def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):

        token = None
        if 'authorization' in request.headers:
            token = request.headers['authorization']
        if not token:
            return make_response(jsonify({'message': 'a valid token is missing'}), 400)

        try:
            data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
           # print(data['email'])
            current_user = db.session.query(User).filter_by(email=data['email']).first()
            #print(current_user.id)

        except Exception as ex:
            print(ex)
            return make_response(jsonify({'message': 'token is invalid'}), 400)

        return f(current_user, *args, **kwargs)

    return decorator


def admin_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None

        if 'authorization' in request.headers:
            token = request.headers['authorization']
        if not token:
            return make_response(jsonify({'message': 'a valid token is missing'}), 400)

        try:
            data = jwt.decode(token.split(" ")[1], current_app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = db.session.query(User).filter_by(email=data['email']).first()
            if not current_user.admin:
                return make_response(jsonify({'message': 'user is not an admin'}), 403)

        except Exception as ex:
            print(ex)
            return make_response(jsonify({'message': 'token is invalid'}), 400)


        return f(current_user, *args, **kwargs)

    return decorator


def email_validation(email):
    EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

    if re.search(EMAIL_REGEX, email):
        return True

    return False


def username_validation(username):
    # Remove leading and trailing blanks
    username = username.strip()

    if username.len() < 20 & username.len() > 5:
        return True

    return False


def name_validation(name):
    # Regex to reject names with double spaces between words and names with numbers
    NAME_REGEX = "/^([a-zA-Z]+\s)*[a-zA-Z]+$/"

    # Remove leading and trailing blanks
    name = name.strip()

    if re.search(NAME_REGEX, name) & name.len() < 32 & name.len() > 1:
        return True

    return False


def password_validation(password):
    # Regex for strong password
    # At least one lowercase letter (?=.*[a-z])
    # At least one uppercase letter (?=.*[A-Z])
    # At least one digit (?=.*[0-9])
    # At least one special character (?=.*[^A-Za-z0-9])
    # Eight characters or longer (?=.{8,})
    PASSWORD_REGEX = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,})"

    if re.search(PASSWORD_REGEX, password):
        return True

    return False


def password_confirmation(password, passwordConfirmation):
    if password == passwordConfirmation:
        return True

    return False


    # Generate Password Token
#    def get_reset_password_token(self, expires_in=1440):
#        return jwt.encode(
#            {'reset_password': self.id, 'exp': time() + expires_in},
#            #app.config['SECRET_KEY'], algorithm='HS256' TODO Generating an error bc i cant import app.py
#        )

    # Verify Password Token
#    @staticmethod
#    def verify_reset_password_token(token):
#        try:
#        #id = jwt.decode(token, app.config['SECRET_KEY'],
#            #                algorithms=['HS256'])['reset_password']
#        except:
#            return

#        return Utilizador.query.get(id)


# endregion