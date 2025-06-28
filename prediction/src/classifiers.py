import pickle
from abc import ABC
from enum import Enum
from typing import Final, Union, final

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier, AdaBoostClassifier, GradientBoostingClassifier
from sklearn.model_selection import GridSearchCV, RandomizedSearchCV
from sklearn.tree import DecisionTreeClassifier

from .configuration import PredictorConfiguration


@final
class PredictorClassifierType(Enum):
    DECISION_TREE: Final[int] = 1
    RANDOM_FOREST: Final[int] = 2
    ADA_BOOST: Final[int] = 3
    GRADIENT_BOOSTING: Final[int] = 4


class PredictorClassifier(ABC):
    def __init__(self,
                 prediction_classifier_type: PredictorClassifierType,
                 configuration: PredictorConfiguration,
                 **kwargs) -> None:
        self.prediction_classifier_type = prediction_classifier_type
        self.cv_: Union[GridSearchCV, RandomizedSearchCV] = configuration.search_method.cls(**kwargs,
                                                                                            cv=configuration.CROSS_VALIDATOR,
                                                                                            scoring=configuration.SCORING,
                                                                                            n_jobs=configuration.GS_N_JOBS,
                                                                                            refit=True)

    def fit(self, x: pd.DataFrame, y: pd.Series, sample_weight: np.ndarray) -> None:
        self.cv_.fit(x, y, sample_weight=sample_weight)

    def predict(self, x: pd.DataFrame) -> np.ndarray:
        return self.cv_.best_estimator_.predict(x)

    def predict_proba(self, x: pd.DataFrame) -> np.ndarray:
        return self.cv_.best_estimator_.predict_proba(x)

    def export(self, file_path: str) -> None:
        with open(file_path, 'wb') as file:
            pickle.dump(self, file)

    @staticmethod
    def load(file_path: str) -> 'PredictorClassifier':
        with open(file_path, 'rb') as file:
            return pickle.load(file)

    @property
    def best_score(self) -> float:
        return self.cv_.best_score_

    @property
    def best_params(self) -> dict:
        return self.cv_.best_params_


@final
class DecisionTreePredictorClassifier(PredictorClassifier):

    def __init__(self, configuration: PredictorConfiguration) -> None:
        kwargs = {
            'estimator': DecisionTreeClassifier(criterion='gini', random_state=configuration.RANDOM_STATE),
            configuration.search_method.param_key: {
                'min_samples_leaf': [1] + list(range(5, 51, 5)),
                'max_features': [10, 20] + list(range(50, 501, 50))
            }
        }
        super().__init__(PredictorClassifierType.DECISION_TREE, configuration, **kwargs)


@final
class RandomForestPredictorClassifier(PredictorClassifier):

    def __init__(self, configuration: PredictorConfiguration) -> None:
        kwargs = {
            'estimator': RandomForestClassifier(criterion='gini', warm_start=True,
                                                random_state=configuration.RANDOM_STATE),
            configuration.search_method.param_key: {
                'n_estimators': [10, 50] + list(range(100, 251, 50)),
                'min_samples_leaf': [1] + list(range(5, 51, 5))
            }
        }
        super().__init__(PredictorClassifierType.RANDOM_FOREST, configuration, **kwargs)


@final
class AdaBoostPredictorClassifier(PredictorClassifier):

    def __init__(self, configuration: PredictorConfiguration) -> None:
        kwargs = {
            'estimator': AdaBoostClassifier(random_state=configuration.RANDOM_STATE),
            configuration.search_method.param_key: {
                'estimator': [
                    DecisionTreeClassifier(max_depth=1, random_state=configuration.RANDOM_STATE),
                    DecisionTreeClassifier(max_depth=2, random_state=configuration.RANDOM_STATE),
                ],
                'n_estimators': [100, 200, 300],
                'learning_rate': [0.1, 0.5, 1.0, 2.0]
            }
        }
        super().__init__(PredictorClassifierType.ADA_BOOST, configuration, **kwargs)


@final
class GradientBoostingPredictorClassifier(PredictorClassifier):

    def __init__(self, configuration: PredictorConfiguration) -> None:
        kwargs = {
            'estimator': GradientBoostingClassifier(warm_start=True, random_state=configuration.RANDOM_STATE),
            configuration.search_method.param_key: {
                'n_estimators': [100, 200, 300],
                'learning_rate': [0.1, 0.5, 1.0, 2.0]
            }
        }
        super().__init__(PredictorClassifierType.GRADIENT_BOOSTING, configuration, **kwargs)


def get_predictor_classifiers(configuration: PredictorConfiguration) -> list[PredictorClassifier]:
    return [
        DecisionTreePredictorClassifier(configuration),
        RandomForestPredictorClassifier(configuration),
        AdaBoostPredictorClassifier(configuration),
        GradientBoostingPredictorClassifier(configuration),
    ]
