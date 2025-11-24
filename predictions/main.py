import logging

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException

from predictions import service
from predictions.types import PredictionsInferRequest, PlayerPredictionsResponse, PredictionsModelInfo

logging.basicConfig(level=logging.INFO, format="[%(asctime)s][%(name)s][%(levelname)s] - %(message)s")
logger = logging.getLogger("predictions")

# Loading env variables
load_dotenv()

# Setting up FastAPI instance
app = FastAPI()


@app.get("/health")
def health_check():
    return "OK"


@app.post("/api/predictions-model/{game_map_id}/train")
def train_predictions_model(game_map_id: str) -> PredictionsModelInfo:
    logger.info(f"Training predictions model for game map ID: '{game_map_id}'")
    try:
        return service.train_predictions_model(game_map_id)
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=500, detail="Unable to train the predictions model!")


@app.post("/api/predictions-model/{game_map_id}/infer")
def infer_predictions_model(game_map_id: str, body: PredictionsInferRequest) -> PlayerPredictionsResponse:
    logger.info(f"Inferring predictions model for game map ID: '{game_map_id}'")
    try:
        player_predictions = service.infer_predictions_model(game_map_id, body.data)
        return PlayerPredictionsResponse(player_predictions=player_predictions)
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=500, detail="Unable to infer the predictions model!")


@app.get("/api/predictions-model/{game_map_id}")
def get_predictions_model(game_map_id: str) -> PredictionsModelInfo:
    logger.info(f"Fetching latest predictions model for game map ID: '{game_map_id}'")
    try:
        return service.get_predictions_model_latest_info(game_map_id)
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=404, detail="No predictions model found! Try training the model.")
