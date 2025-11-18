from datetime import datetime

from flask_login import UserMixin
from werkzeug.security import check_password_hash, generate_password_hash

from . import db, login_manager


class TimestampMixin:
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)


class User(UserMixin, db.Model, TimestampMixin):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    role = db.Column(db.String(20), nullable=False)  # student / technician / admin
    is_active = db.Column(db.Boolean, default=True)

    requests = db.relationship("IssueRequest", back_populates="requester", lazy=True)

    def set_password(self, password: str) -> None:
        self.password_hash = generate_password_hash(password)

    def check_password(self, password: str) -> bool:
        return check_password_hash(self.password_hash, password)

    def get_id(self):
        return str(self.id)


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))


class Category(db.Model, TimestampMixin):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    parent_id = db.Column(db.Integer, db.ForeignKey("category.id"), nullable=True)

    parent = db.relationship("Category", remote_side=[id], backref="children", lazy=True)


class Material(db.Model, TimestampMixin):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    specification = db.Column(db.String(120))
    unit = db.Column(db.String(20), default="pcs")
    price = db.Column(db.Float, default=0)
    safety_stock = db.Column(db.Integer, default=0)
    description = db.Column(db.Text)

    category_id = db.Column(db.Integer, db.ForeignKey("category.id"))
    category = db.relationship("Category", backref="materials", lazy=True)

    current_stock = db.Column(db.Integer, default=0)

    inbound_records = db.relationship("InboundRecord", back_populates="material", lazy=True)
    issue_requests = db.relationship("IssueRequest", back_populates="material", lazy=True)


class InboundRecord(db.Model, TimestampMixin):
    id = db.Column(db.Integer, primary_key=True)
    material_id = db.Column(db.Integer, db.ForeignKey("material.id"), nullable=False)
    quantity = db.Column(db.Integer, nullable=False)
    supplier = db.Column(db.String(120))
    note = db.Column(db.String(255))
    received_at = db.Column(db.Date, default=datetime.utcnow)

    material = db.relationship("Material", back_populates="inbound_records")


class IssueRequest(db.Model, TimestampMixin):
    id = db.Column(db.Integer, primary_key=True)
    material_id = db.Column(db.Integer, db.ForeignKey("material.id"), nullable=False)
    requester_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False)
    quantity = db.Column(db.Integer, nullable=False)
    purpose = db.Column(db.String(255))
    course_code = db.Column(db.String(120))
    status = db.Column(db.String(20), default="pending")  # pending / approved / rejected
    reviewer_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=True)
    reviewed_at = db.Column(db.DateTime)
    rejection_reason = db.Column(db.String(255))

    material = db.relationship("Material", back_populates="issue_requests", foreign_keys=[material_id])
    requester = db.relationship("User", back_populates="requests", foreign_keys=[requester_id])
    reviewer = db.relationship("User", foreign_keys=[reviewer_id])
