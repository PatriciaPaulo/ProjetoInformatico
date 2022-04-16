from flask import Flask
from routes import routes_blueprint
from models import db

app = Flask(__name__)
app.config['SECRET_KEY']='6e129cb9707e18357de8b945656c430f'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///spl.sqlite'
app.register_blueprint(routes_blueprint, url_prefix='/api')
db.init_app(app)
with app.app_context():
    db.create_all()

if __name__ == '__main__':

    app.run(debug=True)


