from http.client import HTTPException

from fastapi import APIRouter, Depends,HTTPException
from sqlalchemy.orm import Session
from config.database import get_db
from models.ai_result import AIRequestDTO,AIResponseDTO,AIResult
from service.ai_service import AIService\



router = APIRouter(prefix="/ai", tags=["ai"])

@router.get("/", response_model=list[AIResponseDTO])
def get_results(db: Session = Depends(get_db)):
    return AIService.get_all_ai_results(db)

@router.post("/", response_model=AIResponseDTO)
def add_result(result: AIRequestDTO, db: Session = Depends(get_db)):
    ai_result = AIService.add_ai_result(db,result)

    response = AIResponseDTO(
        id=ai_result.id,
        result=ai_result.result,
        prediction=ai_result.prediction,
        accuracy=ai_result.accuracy
    )

    return response

@router.get("/{result_id}", response_model=AIResponseDTO)
def get_result_by_id(result_id: int, db: Session = Depends(get_db)):
    result = AIService.get_ai_result_by_id(db, result_id)
    if result is None:
        raise HTTPException(status_code=404, detail="Result not found")
    return result