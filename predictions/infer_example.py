import os
from typing import Optional

import requests
from dotenv import load_dotenv

from predictions.constants import HISTORY_DATA_DIR
from predictions.data import load_update_payloads
from predictions.mlflow import setup_mlflow
from predictions.types import PredictionsInferRequest

GAME_MAP_ID = "india"
HISTORY_FILE_NAME = ""

# If you have provided a history file for a complete game, meaning, it will have `WIN` update type in the end, then,
# ensure that you provide a few steps before when at least more than 1 player is `ACTIVE` for this script to work.
UNTIL_GAME_STEP: Optional[int] = None

# Loading environment variables for MLFlow
load_dotenv()

# Setting up MLFlow
setup_mlflow()

# Creating inference data from history
history_data_dir = os.getenv(HISTORY_DATA_DIR)
update_payloads = load_update_payloads(os.path.join(history_data_dir, HISTORY_FILE_NAME))

# Filtering payloads til requested game step
if UNTIL_GAME_STEP:
    update_payloads = [update_payload for update_payload in update_payloads if
                       update_payload["gameStep"] <= UNTIL_GAME_STEP]

request = PredictionsInferRequest(data=update_payloads)
request_json = request.model_dump_json()

try:
    response = requests.post(f"http://localhost:8003/api/predictions-model/{GAME_MAP_ID}/infer", request_json)
    print(response.json())
except Exception as e:
    print(e)
