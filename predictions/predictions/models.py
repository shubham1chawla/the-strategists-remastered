from enum import Enum
from typing import Optional, Any

import mlflow.pyfunc
import pandas as pd
from catboost import CatBoostClassifier
from lightgbm import LGBMClassifier
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.pipeline import Pipeline
from xgboost import XGBClassifier

from predictions.types import Prediction


class Classifier(Enum):
    RANDOM_FOREST = RandomForestClassifier
    GRADIENT_BOOSTING = GradientBoostingClassifier
    XGBOOST = XGBClassifier
    LIGHTGBM = LGBMClassifier
    CATBOOST = CatBoostClassifier

    def params(self, *, random_state: int):
        base_params = {"random_state": random_state}
        match self:
            case Classifier.RANDOM_FOREST | Classifier.GRADIENT_BOOSTING | Classifier.XGBOOST:
                return base_params
            case Classifier.LIGHTGBM:
                return {
                    **base_params,
                    "boosting_type": "rf",
                    "subsample_freq": 1,
                    "subsample": 0.8,
                    "verbosity": -1,
                }
            case Classifier.CATBOOST:
                return {
                    **base_params,
                    "allow_writing_files": False,
                    "verbose": False,
                }
            case _:
                raise KeyError(f"Invalid classifier: {self.name}")

    def build(self, *, random_state: int):
        params = self.params(random_state=random_state)
        return self.value(**params)


class PredictionsModel(mlflow.pyfunc.PythonModel):
    def __init__(self, model: Pipeline):
        super().__init__()
        self.model = model

    def predict(self, context, model_input: pd.DataFrame, params: Optional[dict[str, Any]] = None) -> pd.DataFrame:
        # Getting predictions
        predictions = self.model.predict(model_input)
        proba = self.model.predict_proba(model_input)

        # Preparing output with predictions and proba
        output = {"prediction": predictions}
        for i, class_ in enumerate(self.model.classes_):
            output[Prediction.from_model_output(class_).probability_label] = proba[:, i]

        # Creating final output dataframe and applying player ID index
        output = pd.DataFrame(output, index=model_input.index)

        # Converting player status back to ACTIVE or BANKRUPT
        output["prediction"] = output["prediction"].apply(lambda l: Prediction.from_model_output(l))
        return output
