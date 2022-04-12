from flask import Flask
from routes import routes_blueprint
from models import db

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///spl.sqlite'
app.register_blueprint(routes_blueprint, url_prefix='/api')
db.init_app(app)


if __name__ == '__main__':
    app.run(debug=True)


