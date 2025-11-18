from pathlib import Path

from flask import Flask
from flask_login import LoginManager
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash

# Global extensions
login_manager = LoginManager()
db = SQLAlchemy()


def create_app(test_config=None):
    """Application factory."""
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        SECRET_KEY="dev-secret-key",
        SQLALCHEMY_DATABASE_URI="sqlite:///%s" % (Path(app.instance_path) / "app.db"),
        SQLALCHEMY_TRACK_MODIFICATIONS=False,
    )

    if test_config:
        app.config.update(test_config)

    # Ensure instance folder exists
    Path(app.instance_path).mkdir(parents=True, exist_ok=True)

    db.init_app(app)
    login_manager.init_app(app)
    login_manager.login_view = "main.login"

    from . import models  # noqa: F401 - ensure models are registered
    from .routes import bp as main_bp, ROLE_LABELS

    @app.context_processor
    def inject_role_label():
        from flask_login import current_user

        if current_user.is_authenticated:
            return {"role_label": ROLE_LABELS.get(current_user.role, current_user.role)}
        return {"role_label": None}

    app.register_blueprint(main_bp)

    @app.cli.command("create-admin")
    def create_admin():
        """Create a default admin account if it does not exist."""
        from .models import User

        username = "admin"
        password = "123456"
        if not User.query.filter_by(username=username).first():
            user = User(username=username, role="admin")
            user.password_hash = generate_password_hash(password)
            db.session.add(user)
            db.session.commit()
            print("Admin account created: admin / 123456")
        else:
            print("Admin account already exists")

    return app
