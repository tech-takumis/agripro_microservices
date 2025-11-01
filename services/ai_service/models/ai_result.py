from sqlalchemy import Column, Integer, String
from config.database import Base
from pydantic import BaseModel

class AIResult(Base):
    __tablename__ = "ai_result"
    id = Column(Integer, primary_key=True, index=True)
    result = Column(String)
    prediction = Column(String)
    accuracy = Column(String)

class AIRequestDTO(BaseModel):
    id: int | None = None
    result: str
    prediction: str
    accuracy: str

    class Config:
        orm_mode = True

class AIResponseDTO(BaseModel):
    id: int
    result: str
    prediction: str
    accuracy: str

    model_config = {'from_attributes': True}