from enum import Enum
from typing import Any, List

from pydantic import BaseModel


class PredictionsModelInfo(BaseModel):
    model_id: str
    model_name: str
    model_version: int


class PredictionsInferRequest(BaseModel):
    data: List[dict[str, Any]]


class Prediction(str, Enum):
    WINNER = "WINNER"
    BANKRUPT = "BANKRUPT"

    @property
    def probability_label(self) -> str:
        return self.lower() + "_probability"

    @classmethod
    def from_model_output(cls, output: int) -> 'Prediction':
        match output:
            case 1:
                return Prediction.WINNER
            case 0:
                return Prediction.BANKRUPT
            case _:
                raise KeyError(f"Invalid model output: {output}")


class PlayerPredictionResponse(BaseModel):
    game_code: str
    player_id: int
    prediction: Prediction
    winner_probability: float
    bankrupt_probability: float


class PlayerPredictionsResponse(BaseModel):
    player_predictions: List[PlayerPredictionResponse]
