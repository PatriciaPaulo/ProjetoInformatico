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

        #userDefault = Utilizador(username="user", password=generate_password_hash("123"), name="Nocme", email="email", admin=False, blocked=False)
        #adminDefault = Utilizador(username="admin", password=generate_password_hash("123"), name="Nocme", email="email",admin=True, blocked=False)
        #lixeira1 = Lixeira(latitude="38.0",longitude="-9",criador=userDefault.username,estado="limpo",aprovado=False,foto="asdas")
        #lixeira2 = Lixeira(latitude="38",longitude="-9",criador=userDefault.username,estado="sujo",aprovado=False,foto="asdas")
        #session.add(adminDefault)
        #session.add(userDefault)
        #db.session.add(lixeira1)
        #db.session.add(lixeira2)
        #session.commit()
        app.run(debug=True)


