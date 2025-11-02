from sqlalchemy.orm import Session
from models.ai_result import AIResult, AIResponseDTO, AIRequestDTO


class AIService:
    @staticmethod
    def get_all_ai_results(db: Session):
        results = db.query(AIResult).all()

        return [AIResponseDTO.model_validate(r) for r in results]

    @staticmethod
    def add_ai_result(db: Session, result: AIRequestDTO):
        db.add(result)
        db.commit()
        db.refresh(result)

    @staticmethod
    def get_ai_result_by_id(db: Session, result_id: int):
        result = db.query(AIResult).get(result_id)
        if result is None:
            return None
        return AIResponseDTO.model_validate(result)
