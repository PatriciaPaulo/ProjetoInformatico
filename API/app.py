from flask import Flask

from routes import routes_blueprint
from models import db, Utilizador, Lixeira
from werkzeug.security import generate_password_hash

from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from models import Base

if __name__ == '__main__':
    app = Flask(__name__)
    app.config['SECRET_KEY'] = '6e129cb9707e18357de8b945656c430f'
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///spl.db'
    app.register_blueprint(routes_blueprint, url_prefix='/api')

    db.init_app(app)
    with app.app_context():
        engine = create_engine('sqlite:///spl.db')
        #Base.metadata.drop_all(engine)
        #Base.metadata.create_all(engine)

        session = Session(engine)


        session.commit()
        app.run(debug=True)


