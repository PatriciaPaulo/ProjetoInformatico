from flask import Flask

from routes import routes_blueprint
from models import db, Utilizador, Lixeira
from werkzeug.security import generate_password_hash

if __name__ == '__main__':
    app = Flask(__name__)
    app.config['SECRET_KEY'] = '6e129cb9707e18357de8b945656c430f'
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///spl.sqlite'
    app.register_blueprint(routes_blueprint, url_prefix='/api')

    db.init_app(app)
    with app.app_context():
        db.drop_all()
        db.create_all()
        userDefault = Utilizador(username="user", password=generate_password_hash("123"), name="Nocme", email="email",
                                  admin=False, blocked=False)
        adminDefault = Utilizador(username="admin", password=generate_password_hash("123"), name="Nocme", email="email",
                                  admin=True, blocked=False)

        lixeira1 = Lixeira(localizacao="AAA",criador=userDefault.username,estado="limpo",aprovado=False,foto="asdas")
        lixeira2 = Lixeira(localizacao="AAA",criador=userDefault.username,estado="sujo",aprovado=False,foto="asdas")
        db.session.add(adminDefault)
        db.session.add(userDefault)
        db.session.add(lixeira1)
        db.session.add(lixeira2)
        db.session.commit()
    app.run(debug=True)


