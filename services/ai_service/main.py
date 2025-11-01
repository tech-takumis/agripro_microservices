from fastapi import FastAPI
from config.database import Base,engine
from controller import ai_controller

app = FastAPI()

app.include_router(ai_controller.router)

# Create database tables on startup
@app.on_event("startup")
def startup():
    Base.metadata.create_all(bind=engine)