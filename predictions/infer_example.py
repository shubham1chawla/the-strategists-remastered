import os

import mlflow
import numpy as np
import requests
from dotenv import load_dotenv
from mlflow.utils.proto_json_utils import dataframe_from_raw_json

from predictions.constants import MODEL_NAME_PREFIX
from predictions.mlflow import setup_mlflow, get_predictions_model_latest_version
from predictions.types import PredictionsInferRequest

GAME_MAP_ID = "india"

# Loading environment variables for MLFlow
load_dotenv()

# Setting up MLFlow
setup_mlflow()

# Getting model's latest version
model_name = MODEL_NAME_PREFIX + GAME_MAP_ID
latest_version = get_predictions_model_latest_version(model_name)
artifact_uri = f"models:/{model_name}/{latest_version.version}/input_example.json"

# Downloading input example
input_example_path = mlflow.artifacts.download_artifacts(artifact_uri=artifact_uri, dst_path=".")

# Preparing model input
df = dataframe_from_raw_json(input_example_path)
df["game.code"] = "ABCD"
df["player.id"] = np.arange(1, df.shape[0] + 1)

request = PredictionsInferRequest(data=df.to_dict(orient="records"))
request_json = request.model_dump_json()

try:
    response = requests.post(f"http://localhost:8003/api/predictions-model/{GAME_MAP_ID}/infer", request_json)
    print(response.json())
except Exception as e:
    print(e)
finally:
    os.remove(input_example_path)
