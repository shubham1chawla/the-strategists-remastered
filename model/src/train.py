from os import listdir, access, W_OK, path
from os.path import exists, isdir
import pickle
import pandas as pd
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.utils.class_weight import compute_sample_weight

"""
This file contains the class to train the model.
"""

MODEL_FILE_NAME = 'boosting_model.sav'


class Train:
    def __init__(self, data_directory: str, model_directory: str) -> None:
        self.__data_directory__ = data_directory
        self.__model_directory__ = model_directory
        self.__validate_paths__()

    def __validate_paths__(self) -> None:
        # checking if paths are directories
        if not isdir(self.__data_directory__):
            raise OSError(f'Data directory {self.__data_directory__} is not a directory!')
        elif not isdir(self.__model_directory__):
            raise OSError(f'Model directory {self.__model_directory__} is not a directory!')

        # checking if data directory exists
        if not exists(self.__data_directory__):
            raise OSError(f'Data directory {self.__data_directory__} doesn\'t exists!')

        # checking if model directory exists or the script can create
        if not access(self.__model_directory__, W_OK):
            raise OSError(f'Model directoy {self.__model_directory__} doesn\'t have write access!')

    def prepare_model(self) -> None:
        data_files = []
        for i, filename in enumerate(listdir(self.__data_directory__)):
            data_files.append(path.join(self.__data_directory__, filename))
            print(f'{i} - {filename}')

        all_games_data = pd.concat([pd.read_csv(f) for f in data_files], ignore_index=True)

        all_games_data['is_winner'] = all_games_data['player.state'].apply(lambda x: 1 if x == 'ACTIVE' else 0)

        debit_columns = [col for col in all_games_data.columns if col.startswith('debit.')]

        credit_columns = [col for col in all_games_data.columns if col.startswith('credit.')]

        features = ['player.base-cash'] + debit_columns + credit_columns

        X = all_games_data[features]
        y = all_games_data['is_winner']

        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=42)

        sample_weights = compute_sample_weight(class_weight='balanced', y=y_train)

        param_grid = {
            'n_estimators': [50, 100, 200],
            'max_depth': [3, 5, 10],
            'min_samples_leaf': [1, 2, 4]
        }

        grid_search = GridSearchCV(estimator=GradientBoostingClassifier(random_state=42),
                                   param_grid=param_grid,
                                   scoring='f1',
                                   cv=5)

        grid_search.fit(X_train, y_train, sample_weight=sample_weights)

        filename = path.join(self.__model_directory__, MODEL_FILE_NAME)
        pickle.dump(grid_search.best_estimator_, open(filename, 'wb'))
