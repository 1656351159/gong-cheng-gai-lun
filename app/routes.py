from datetime import datetime
from io import StringIO

from flask import (Blueprint, Response, flash, redirect, render_template, request,
                   url_for)
from flask_login import current_user, login_required, login_user, logout_user

from . import db
from .models import Category, InboundRecord, IssueRequest, Material, User

bp = Blueprint("main", __name__)


ROLE_LABELS = {
    "student": "学生",
    "technician": "实验员",
    "admin": "管理员",
}


def role_required(*roles):
    def decorator(func):
        from functools import wraps

        @wraps(func)
        def wrapper(*args, **kwargs):
            if not current_user.is_authenticated:
                return redirect(url_for("main.login"))
            if roles and current_user.role not in roles:
                flash("您没有权限执行此操作", "danger")
                return redirect(url_for("main.dashboard"))
            return func(*args, **kwargs)

        return wrapper

    return decorator


@bp.route("/")
@login_required
def dashboard():
    materials_count = Material.query.count()
    pending_requests = IssueRequest.query.filter_by(status="pending").count()
    total_stock = db.session.query(db.func.sum(Material.current_stock)).scalar() or 0
    return render_template(
        "dashboard.html",
        materials_count=materials_count,
        pending_requests=pending_requests,
        total_stock=total_stock,
        role_label=ROLE_LABELS.get(current_user.role, current_user.role),
    )


@bp.route("/login", methods=["GET", "POST"])
def login():
    if current_user.is_authenticated:
        return redirect(url_for("main.dashboard"))

    if request.method == "POST":
        username = request.form.get("username")
        password = request.form.get("password")
        user = User.query.filter_by(username=username, is_active=True).first()
        if user and user.check_password(password):
            login_user(user)
            return redirect(url_for("main.dashboard"))
        flash("登录失败，请检查用户名或密码", "danger")
    return render_template("login.html")


@bp.route("/logout")
@login_required
def logout():
    logout_user()
    return redirect(url_for("main.login"))


@bp.route("/categories", methods=["GET", "POST"])
@login_required
@role_required("admin")
def categories():
    if request.method == "POST":
        name = request.form.get("name")
        parent_id = request.form.get("parent_id") or None
        if parent_id is not None:
            parent_id = int(parent_id)
        if name:
            category = Category(name=name, parent_id=parent_id)
            db.session.add(category)
            db.session.commit()
            flash("类目已创建", "success")
        else:
            flash("名称不能为空", "danger")
    all_categories = Category.query.all()
    return render_template("categories.html", categories=all_categories)


@bp.route("/materials", methods=["GET", "POST"])
@login_required
@role_required("admin")
def materials():
    if request.method == "POST":
        data = request.form
        category_id = data.get("category_id") or None
        if category_id is not None and category_id != "":
            category_id = int(category_id)
        else:
            category_id = None
        material = Material(
            name=data.get("name"),
            specification=data.get("specification"),
            unit=data.get("unit"),
            price=float(data.get("price") or 0),
            safety_stock=int(data.get("safety_stock") or 0),
            description=data.get("description"),
            category_id=category_id,
        )
        db.session.add(material)
        db.session.commit()
        flash("材料已创建", "success")
    search = request.args.get("search")
    query = Material.query
    if search:
        query = query.filter(Material.name.ilike(f"%{search}%"))
    return render_template(
        "materials.html",
        materials=query.order_by(Material.name).all(),
        categories=Category.query.all(),
    )


@bp.route("/inbound", methods=["GET", "POST"])
@login_required
@role_required("technician", "admin")
def inbound():
    if request.method == "POST":
        material_id = int(request.form.get("material_id"))
        quantity = int(request.form.get("quantity") or 0)
        supplier = request.form.get("supplier")
        note = request.form.get("note")
        received_at = request.form.get("received_at")
        if quantity <= 0:
            flash("数量必须大于 0", "danger")
        else:
            record = InboundRecord(
                material_id=material_id,
                quantity=quantity,
                supplier=supplier,
                note=note,
                received_at=datetime.strptime(received_at, "%Y-%m-%d") if received_at else datetime.utcnow(),
            )
            material = Material.query.get(material_id)
            material.current_stock += quantity
            db.session.add(record)
            db.session.commit()
            flash("入库成功", "success")
    records = InboundRecord.query.order_by(InboundRecord.created_at.desc()).all()
    return render_template("inbound.html", records=records, materials=Material.query.all())


@bp.route("/requests/new", methods=["GET", "POST"])
@login_required
@role_required("student")
def new_request():
    if request.method == "POST":
        quantity = int(request.form.get("quantity") or 0)
        material_id = int(request.form.get("material_id"))
        material = Material.query.get(material_id)
        if material is None:
            flash("材料不存在", "danger")
        elif quantity <= 0:
            flash("数量必须大于 0", "danger")
        elif quantity > material.current_stock:
            flash("库存不足，无法提交申请", "danger")
        else:
            req = IssueRequest(
                material_id=material_id,
                requester_id=current_user.id,
                quantity=quantity,
                purpose=request.form.get("purpose"),
                course_code=request.form.get("course_code"),
            )
            db.session.add(req)
            db.session.commit()
            flash("申请已提交", "success")
            return redirect(url_for("main.list_requests"))
    return render_template("new_request.html", materials=Material.query.order_by(Material.name).all())


@bp.route("/requests")
@login_required
def list_requests():
    if current_user.role == "student":
        requests_q = IssueRequest.query.filter_by(requester_id=current_user.id)
    else:
        requests_q = IssueRequest.query
    requests_q = requests_q.order_by(IssueRequest.created_at.desc())
    return render_template("requests.html", requests=requests_q.all())


@bp.route("/requests/<int:request_id>/approve", methods=["POST"])
@login_required
@role_required("technician", "admin")
def approve_request(request_id):
    req = IssueRequest.query.get_or_404(request_id)
    if req.status != "pending":
        flash("该申请已处理", "info")
        return redirect(url_for("main.list_requests"))
    material = req.material
    if req.quantity > material.current_stock:
        flash("库存不足，无法通过", "danger")
        return redirect(url_for("main.list_requests"))
    material.current_stock -= req.quantity
    req.status = "approved"
    req.reviewer_id = current_user.id
    req.reviewed_at = datetime.utcnow()
    db.session.commit()
    flash("已通过领用申请", "success")
    return redirect(url_for("main.list_requests"))


@bp.route("/requests/<int:request_id>/reject", methods=["POST"])
@login_required
@role_required("technician", "admin")
def reject_request(request_id):
    req = IssueRequest.query.get_or_404(request_id)
    if req.status != "pending":
        flash("该申请已处理", "info")
        return redirect(url_for("main.list_requests"))
    req.status = "rejected"
    req.reviewer_id = current_user.id
    req.reviewed_at = datetime.utcnow()
    req.rejection_reason = request.form.get("reason")
    db.session.commit()
    flash("已驳回领用申请", "warning")
    return redirect(url_for("main.list_requests"))


@bp.route("/stock")
@login_required
@role_required("technician", "admin")
def stock():
    materials = Material.query.order_by(Material.name).all()
    return render_template("stock.html", materials=materials)


@bp.route("/export/requests")
@login_required
@role_required("admin")
def export_requests():
    output = StringIO()
    output.write("申请ID,材料,数量,申请人,状态,用途,课程/项目,时间\n")
    for req in IssueRequest.query.order_by(IssueRequest.created_at.desc()).all():
        output.write(
            f"{req.id},{req.material.name},{req.quantity},{req.requester.username},{req.status},{req.purpose or ''},{req.course_code or ''},{req.created_at:%Y-%m-%d %H:%M}\n"
        )
    output.seek(0)
    return Response(
        output.getvalue(),
        mimetype="text/csv",
        headers={"Content-Disposition": "attachment; filename=issue_requests.csv"},
    )


@bp.route("/export/stock")
@login_required
@role_required("admin")
def export_stock():
    output = StringIO()
    output.write("材料,规格,库存,单位,单价,安全库存\n")
    for material in Material.query.order_by(Material.name).all():
        output.write(
            f"{material.name},{material.specification or ''},{material.current_stock},{material.unit},{material.price},{material.safety_stock}\n"
        )
    output.seek(0)
    return Response(
        output.getvalue(),
        mimetype="text/csv",
        headers={"Content-Disposition": "attachment; filename=current_stock.csv"},
    )
