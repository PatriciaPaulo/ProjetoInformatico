import jwt
from flask import Flask, request, current_app

from routes.garbagetype_routes import garbagetype_routes_blueprint
from routes.garbagespot_routes import garbagespot_routes_blueprint
from routes.admin_routes import admin_routes_blueprint
from routes.event_routes import event_routes_blueprint
from routes.activity_routes import activity_routes_blueprint
from routes.user_routes import user_routes_blueprint
from routes.userinevent_routes import users_event_routes_blueprint
from routes.friends_routes import friends_routes_blueprint
from routes.messages_routes import messages_routes_blueprint


from models import db, Base, User

from sqlalchemy import create_engine
from sqlalchemy.orm import Session

from websockets_server import start_websockets

SECRET_KEY = '6e129cb9707e18357de8b945656c430f'
engine = create_engine('sqlite:///spl.db')



if __name__ == '__main__':
    app = Flask(__name__)
    app.config['SECRET_KEY'] = SECRET_KEY
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///spl.db'
    # region Register App Routes
    app.register_blueprint(admin_routes_blueprint, url_prefix='/api')
    app.register_blueprint(garbagespot_routes_blueprint, url_prefix='/api')
    app.register_blueprint(event_routes_blueprint, url_prefix='/api')
    app.register_blueprint(activity_routes_blueprint, url_prefix='/api')
    app.register_blueprint(user_routes_blueprint, url_prefix='/api')
    app.register_blueprint(garbagetype_routes_blueprint, url_prefix='/api')
    app.register_blueprint(users_event_routes_blueprint, url_prefix='/api')
    app.register_blueprint(friends_routes_blueprint, url_prefix='/api')
    app.register_blueprint(messages_routes_blueprint, url_prefix='/api')
    # endregion

    #mail = Mail(app)
    db.init_app(app)

    start_websockets()
    with app.app_context():
        #Base.metadata.drop_all(engine)
        #Base.metadata.create_all(engine)

        session = Session(engine)

        session.commit()


        #app.run(host='0.0.0.0', port=5000,debug=True)
        app.run(debug=True, use_reloader=False)



