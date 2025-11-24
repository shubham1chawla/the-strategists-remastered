import logging

import mlflow
from mlflow.environment_variables import MLFLOW_TRACKING_URI
from mlflow.pyfunc import PyFuncModel

from predictions.constants import MODEL_NAME_PREFIX, EXPERIMENT_NAME_SUFFIX

logger = logging.getLogger(__name__)


def setup_mlflow():
    # Checking if tracking uri is provided
    if not MLFLOW_TRACKING_URI.is_set():
        raise KeyError(f"Set '{MLFLOW_TRACKING_URI}' where models should be exported!")


def get_experiment_id(game_map_id: str) -> str:
    client = mlflow.MlflowClient()

    # Loading experiment by name
    experiment_name = MODEL_NAME_PREFIX + game_map_id + EXPERIMENT_NAME_SUFFIX
    experiment = client.get_experiment_by_name(experiment_name)

    # Checking if experiment exists
    if experiment:
        logger.info(f"Found existing experiment: '{experiment_name}'")
        return experiment.experiment_id

    # Creating new experiment
    logger.info(f"Creating new experiment: '{experiment_name}'")
    return client.create_experiment(experiment_name)


def get_predictions_model_latest_version(model_name: str):
    client = mlflow.MlflowClient()

    # Loading latest model version
    versions = client.search_model_versions(f"name='{model_name}'", order_by=["version_number DESC"], max_results=1)
    if not versions:
        raise FileNotFoundError(f"No model versions found under the name '{model_name}'!")

    return versions[0]


def get_predictions_model(game_map_id: str) -> PyFuncModel:
    # Loading latest model versions
    model_name = MODEL_NAME_PREFIX + game_map_id
    latest_version = get_predictions_model_latest_version(model_name)
    logger.info(f"Latest version found for model '{model_name}': {latest_version.version}")

    # Loading model from MLFlow
    model_uri = f"models:/{model_name}/{latest_version.version}"
    logger.info(f"Loading model '{model_uri}'...")

    return mlflow.pyfunc.load_model(model_uri)
