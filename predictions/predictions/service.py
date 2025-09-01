import logging
import os
from typing import Any, List, Tuple, Optional

import mlflow
import numpy as np
import pandas as pd
from mlflow.models import infer_signature
from sklearn.metrics import accuracy_score, roc_auc_score, classification_report, confusion_matrix
from sklearn.model_selection import train_test_split, StratifiedKFold
from sklearn.pipeline import Pipeline
from sklearn.utils import compute_sample_weight

from predictions import constants
from predictions.constants import MODEL_NAME_PREFIX
from predictions.mlflow import get_experiment_id, get_predictions_model, get_predictions_model_latest_version
from predictions.models import Classifier, PredictionsModel
from predictions.pipeline import create_feature_engineering_pipeline
from predictions.types import PlayerPredictionResponse, PredictionsModelInfo

logger = logging.getLogger(__name__)


def _load_training_dataset(game_map_id: str) -> Tuple[int, pd.DataFrame]:
    # Checking if predictions data directory is set
    if constants.PREDICTIONS_DATA_DIR not in os.environ:
        raise KeyError(f"Set '{constants.PREDICTIONS_DATA_DIR}' to a directory containing predictions data!")

    # Validating predictions data directory
    predictions_data_dir = os.getenv(constants.PREDICTIONS_DATA_DIR)
    if not os.path.exists(predictions_data_dir) or not os.path.isdir(predictions_data_dir):
        raise FileNotFoundError(f"'{predictions_data_dir}' either doesn't exist or is not a directory!")

    # Checking if csv files in the data directory
    logger.info(f"Loading CSV files from: '{predictions_data_dir}'")
    csv_paths = [os.path.join(predictions_data_dir, name)
                 for name in os.listdir(predictions_data_dir) if name.startswith(game_map_id) and name.endswith(".csv")]
    if not csv_paths:
        raise FileNotFoundError(f"'{predictions_data_dir}' contains no '.csv' files for game map ID '{game_map_id}'!")
    logger.info(f"Loaded {len(csv_paths)} CSV files for game map ID: '{game_map_id}'")

    # Producing combined dataframe
    df = pd.concat([pd.read_csv(csv_path) for csv_path in csv_paths], ignore_index=True)

    # Preprocessing dataframe
    df = _preprocess_dataframe(df, drop_columns=["game.export.timestamp", "game.bankruptcy-order", "player.username"])

    # Combining csv files
    return len(csv_paths), df


def _fit_classifier(classifier: Classifier, x, y) -> Pipeline:
    # Preparing model
    model = Pipeline([
        ("feature_engineering", create_feature_engineering_pipeline()),
        ("classifier", classifier.build(random_state=constants.RANDOM_STATE)),
    ])

    # Fitting pipeline
    fit_params = {"classifier__sample_weight": compute_sample_weight(class_weight="balanced", y=y)}
    model.fit(x, y, **fit_params)
    return model


def _get_best_classifier(x_train, y_train) -> Classifier:
    best_classifier, best_mean_train_accuracy, best_mean_val_accuracy = None, 0, 0
    for classifier in Classifier:
        logger.info(f"Training {classifier.name}...")
        # Preparing validation sets
        skf = StratifiedKFold(n_splits=constants.N_SPLITS, shuffle=True, random_state=constants.RANDOM_STATE)

        # Calculating train and validation accuracies
        train_accuracies, val_accuracies = [], []
        for train_idx, val_idx in skf.split(x_train, y_train):
            # Extracting subsets of train & validation sets
            x_train_, y_train_ = x_train.iloc[train_idx], y_train.iloc[train_idx]
            x_val, y_val = x_train.iloc[val_idx], y_train.iloc[val_idx]

            # Preparing model
            model = _fit_classifier(classifier, x_train_, y_train_)

            # Adding accuracies to collection for mean calculations
            train_accuracies.append(accuracy_score(y_train_, model.predict(x_train_)))
            val_accuracies.append(accuracy_score(y_val, model.predict(x_val)))

        # Calculating mean accuracies
        mean_train_accuracy, mean_val_accuracy = np.mean(train_accuracies), np.mean(val_accuracies)
        logger.info(f"> mean train accuracy: {mean_train_accuracy:.2f}")
        logger.info(f"> mean val accuracy: {mean_val_accuracy:.2f}")

        # Figuring out if it is the best classifier
        delta = abs(mean_val_accuracy - best_mean_val_accuracy)
        is_val_accuracy_better = mean_val_accuracy > best_mean_val_accuracy
        is_train_accuracy_better = delta < constants.VAL_ACCURACY_DELTA and mean_train_accuracy > best_mean_train_accuracy
        if is_val_accuracy_better or is_train_accuracy_better:
            best_classifier = classifier
            best_mean_train_accuracy = mean_train_accuracy
            best_mean_val_accuracy = mean_val_accuracy
        logger.info(f"{'=' * 50}")

    logger.info(f"Best classifier found: {best_classifier.name}")
    return best_classifier


def train_predictions_model(game_map_id: str) -> PredictionsModelInfo:
    # Loading training dataset
    csv_files_count, df = _load_training_dataset(game_map_id)

    # Extracting features and label
    x = df.drop("player.state", axis=1)
    y = df["player.state"].apply(lambda state: 1 if state == "ACTIVE" else 0)

    # Splitting dataset
    x_train, x_test, y_train, y_test = train_test_split(x, y,
                                                        test_size=constants.TEST_SIZE,
                                                        random_state=constants.RANDOM_STATE,
                                                        stratify=y)

    # Training and finding best classifier
    best_classifier = _get_best_classifier(x_train, y_train)

    # Loading experiment ID from model name
    experiment_id = get_experiment_id(game_map_id)
    logger.info(f"Registering new model under experiment ID: '{experiment_id}'")

    # Starting MLFlow
    with mlflow.start_run(experiment_id=experiment_id):
        # Preparing final model
        model = _fit_classifier(best_classifier, x_train, y_train)

        # Extracting predictions
        train_predictions = model.predict(x_train)
        test_predictions = model.predict(x_test)
        test_proba = model.predict_proba(x_test)

        # Calculating train and test accuracies
        train_accuracy = accuracy_score(y_train, train_predictions)
        test_accuracy = accuracy_score(y_test, test_predictions)
        logger.info(f"> train accuracy: {train_accuracy:.2f}")
        logger.info(f"> test accuracy: {test_accuracy:.2f}")

        # Calculating ROC score
        roc_auc = roc_auc_score(y_test, test_proba[:, 1])
        logger.info(f"> ROC AUC: {roc_auc:.2f}")

        # Calculating classification report
        report = classification_report(y_test, test_predictions, zero_division=0)
        logger.info(f"> Classification Report:\n{report}")

        # Calculating confusion matrix
        matrix = confusion_matrix(y_test, test_predictions)
        logger.info(f"> Confusion Matrix:\n{matrix}")

        logger.info("Logging model's information to mlflow...")

        # Setting up tags
        mlflow.set_tags({
            "classifier_name": best_classifier.name,
            "csv_files_count": csv_files_count,
            "dataset_rows": df.shape[0],
        })

        # Logging params
        mlflow.log_params(best_classifier.params(random_state=constants.RANDOM_STATE))

        # Logging metrics
        mlflow.log_metrics({
            "train_accuracy": train_accuracy,
            "test_accuracy": test_accuracy,
            "roc_auc_score": roc_auc,
        })

        # Logging texts
        mlflow.log_text(report, "classification_report.txt")
        mlflow.log_text(str(matrix), "confusion_matrix.txt")

        # Converting sklearn pipeline to MLFlow-based model
        python_model = PredictionsModel(model)

        # Preparing input data for the model
        x_example = x[x["game.code"].notnull() & x["player.id"].notnull()]
        x_example.set_index(["game.code", "player.id"], inplace=True)

        # Preparing signature and input example
        signature = infer_signature(x_example, python_model.predict({}, x_example))
        input_example = x_example.sample(n=constants.INPUT_EXAMPLE_SIZE, random_state=constants.RANDOM_STATE)

        # Logging final model
        model_name = MODEL_NAME_PREFIX + game_map_id
        logger.info(f"Registering model: '{model_name}'")
        model_info = mlflow.pyfunc.log_model(python_model=python_model,
                                             signature=signature,
                                             input_example=input_example,
                                             registered_model_name=model_name)

        return PredictionsModelInfo(model_id=model_info.model_id,
                                    model_name=model_name,
                                    model_version=model_info.registered_model_version)


def infer_predictions_model(game_map_id: str, data: List[dict[str, Any]]) -> List[PlayerPredictionResponse]:
    # Preparing model's input
    model_input = _preprocess_dataframe(pd.DataFrame(data),
                                        drop_columns=["game.export.timestamp", "game.bankruptcy-order", "player.state"])
    model_input.set_index(["game.code", "player.id"], inplace=True)

    # Loading model from MLFlow
    python_model = get_predictions_model(game_map_id)

    # Inferring model
    output = python_model.predict(model_input)

    # Resetting index to extract player ID
    output.reset_index(inplace=True)
    output.rename({"game.code": "game_code", "player.id": "player_id"}, axis=1, inplace=True)

    # Converting output
    return [PlayerPredictionResponse(**player_prediction) for player_prediction in output.to_dict(orient="records")]


def _preprocess_dataframe(df: pd.DataFrame, *, drop_columns: Optional[List[str]] = None) -> pd.DataFrame:
    # Dropping unwanted features
    if drop_columns:
        df = df[df.columns[~df.columns.isin(drop_columns)]]

    # Converting integer-based columns to double
    return df.astype({
        "ownership.count": "double",
        "debit.invest.count": "double",
        "debit.rent.count": "double",
        "debit.count": "double",
        "credit.rent.count": "double",
        "credit.count": "double",
    })


def get_predictions_model_latest_info(game_map_id: str) -> Optional[PredictionsModelInfo]:
    model_name = MODEL_NAME_PREFIX + game_map_id
    version = get_predictions_model_latest_version(model_name)
    return PredictionsModelInfo(model_id=version.model_id, model_name=model_name, model_version=version.version)
