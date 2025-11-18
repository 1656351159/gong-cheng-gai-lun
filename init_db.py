"""Initialize the SQLite database with demo data."""
from app import create_app, db
from app.models import Category, Material, User

app = create_app()


with app.app_context():
    db.drop_all()
    db.create_all()

    admin = User(username="admin", role="admin")
    admin.set_password("123456")
    technician = User(username="lab", role="technician")
    technician.set_password("123456")
    student = User(username="stu", role="student")
    student.set_password("123456")

    db.session.add_all([admin, technician, student])

    root_cat = Category(name="电子元器件")
    sub_cat = Category(name="电阻", parent=root_cat)
    reagent_cat = Category(name="化学试剂")
    db.session.add_all([root_cat, sub_cat, reagent_cat])

    resistor = Material(
        name="1kΩ 电阻",
        specification="1/4W",
        unit="pcs",
        price=0.2,
        safety_stock=100,
        description="常用碳膜电阻",
        category=sub_cat,
        current_stock=200,
    )
    ethanol = Material(
        name="无水乙醇",
        specification="500ml",
        unit="瓶",
        price=25,
        safety_stock=10,
        description="实验清洁用",
        category=reagent_cat,
        current_stock=20,
    )
    db.session.add_all([resistor, ethanol])

    db.session.commit()
    print("Database initialized with demo data.")
