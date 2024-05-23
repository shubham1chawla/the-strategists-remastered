# Standard Python imports
from enum import Enum
from typing import Final, Union, final
from abc import ABC

# Machine-learning imports
import pickle
import pandas as pd
import numpy as np
from sklearn.base import BaseEstimator
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, AdaBoostClassifier, GradientBoostingClassifier
from sklearn.model_selection import GridSearchCV, RandomizedSearchCV

# Project imports
from .configuration import PredictorConfiguration


@final
class PredictorClassifierType(Enum):
    DECISION_TREE: Final[int] = 1
    RANDOM_FOREST: Final[int] = 2
    ADA_BOOST: Final[int] = 3
    GRADIENT_BOOSTING: Final[int] = 4


class PredictorClassifier(ABC):

    __cv__: Union[GridSearchCV, RandomizedSearchCV]

    def __init__(self, type: PredictorClassifierType, configuration: PredictorConfiguration, **kwargs) -> None:
        self.type = type
        self.__cv__ = configuration.search_method.cls(**kwargs,
                                                        cv=configuration.CROSS_VALIDATOR, 
                                                        scoring=configuration.SCORING, 
                                                        n_jobs=configuration.GS_N_JOBS, 
                                                        refit=True)


    def fit(self, X: pd.DataFrame, y: pd.Series, sample_weight: np.ndarray) -> None:
        self.__cv__.fit(X, y, sample_weight=sample_weight)


    def predict(self, X: pd.DataFrame) -> np.ndarray:
        return self.best_estimator_.predict(X)
    

    def predict_proba(self, X: pd.DataFrame) -> np.ndarray:
        return self.best_estimator_.predict_proba(X)
    

    def export(self, file_path: str) -> None:
        pickle.dump(self, open(file_path, 'wb'))


    @staticmethod
    def load(file_path: str) -> 'PredictorClassifier':
        return pickle.load(open(file_path, 'rb'))


    @property
    def best_estimator_(self) -> BaseEstimator:
        return self.__cv__.best_estimator_


    @property
    def best_score_(self) -> float:
        return self.__cv__.best_score_
    

    @property
    def best_params_(self) -> float:
        return self.__cv__.best_params_


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
            'estimator': RandomForestClassifier(criterion='gini', warm_start=True, random_state=configuration.RANDOM_STATE),
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
            'estimator': AdaBoostClassifier(algorithm='SAMME', random_state=configuration.RANDOM_STATE),
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