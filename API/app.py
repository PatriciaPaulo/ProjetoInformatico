from flask import Flask

from routes.garbagetype_routes import garbagetype_routes_blueprint
from routes.garbagespot_routes import garbagespot_routes_blueprint
from routes.admin_routes import admin_routes_blueprint
from routes.event_routes import event_routes_blueprint
from routes.activity_routes import activity_routes_blueprint
from routes.user_routes import user_routes_blueprint
from routes.userinevent_routes import users_event_routes_blueprint
from routes.friends_routes import friends_routes_blueprint


from models import db, Base

from sqlalchemy import create_engine
from sqlalchemy.orm import Session

if __name__ == '__main__':
    app = Flask(__name__)
    app.config['SECRET_KEY'] = '6e129cb9707e18357de8b945656c430f'
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
    # endregion

    #mail = Mail(app)
    db.init_app(app)

    with app.app_context():
        engine = create_engine('sqlite:///spl.db')
        #Base.metadata.drop_all(engine)
        #Base.metadata.create_all(engine)

        session = Session(engine)

        session.commit()
        app.run(debug=True)
