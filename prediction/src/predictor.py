# Standard Python imports
import json
import logging as log
from os import listdir, path
from typing import final
from dataclasses import dataclass
from datetime import datetime

# Machine-learning imports
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.model_selection import KFold, train_test_split
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, roc_auc_score, roc_curve
from sklearn.utils.class_weight import compute_sample_weight

# Project imports
from src.configuration import PredictorConfiguration
from src.classifiers import PredictorClassifierType, PredictorClassifier
from src.classifiers import get_predictor_classifiers


@final
@dataclass
class PredictorMetadata:
    timestamp: float
    configuration: PredictorConfiguration
    classifier_type: PredictorClassifierType
    score: float
    params: dict
    test_accuracy: float
    roc_auc: float
    classification_report: str
    confusion_matrix: np.ndarray
    cvs_files_count: int
    rows_count: int


    def __init__(self, configuration: PredictorConfiguration, classifier: PredictorClassifier) -> None:
        self.timestamp = int(round(datetime.now().timestamp() * 1000))
        self.configuration = configuration
        self.classifier_type = classifier.type
        self.score = classifier.best_score_
        self.params = classifier.best_params_


    def describe(self) -> None:
        log.info(f'CSV Files: {self.cvs_files_count} | Rows: {self.rows_count}')
        log.info(f'Classifier: {self.classifier_type.name}')
        log.info(f'- Parameters: {self.params}')
        log.info(f'- Score ({self.configuration.SCORING}): {self.score:.2f}')
        log.info(f'- Test Accuracy: {self.test_accuracy:.2f}')
        log.info(f'- Area under ROC curve: {self.roc_auc:.2f}')
        log.info(f'Classification Report:\n{self.classification_report}')
        log.info(f'Confusion Matrix:\n{self.confusion_matrix}')

    
    def export(self) -> None:        
        export_file_path, exportable_dict = self.export_file_path, self.__dict__
        exportable_dict['classifier_type'] = self.classifier_type.name
        exportable_dict['configuration'] = self.configuration.__dict__
        exportable_dict['params'] = str(self.params)
        exportable_dict['confusion_matrix'] = str(self.confusion_matrix)

        with open(export_file_path, 'w') as file:
            file.write(json.dumps(exportable_dict, indent=4))
            log.info(f'Exported metadata: {export_file_path}')


    @property
    def export_file_path(self) -> str:
        return path.join(self.configuration.metadata_directory, self.file_name)


    @property
    def file_name(self) -> str:
        return f'metadata-{self.timestamp}.json'


@final
@dataclass
class Prediction:
    game_code: str
    player_id: int
    proba: list[float]
    predict: int


class Predictor:


    def __init__(self, configuration: PredictorConfiguration) -> None:
        self.__configuration__ = configuration
    

    def export_model(self) -> None:

        # Importing all the CSV files as dataframe
        df, csv_files_count, rows_count = self.import_data()

        # Feature extraction
        X = self.extract_features(df)
        y = df['player.state'].apply(lambda x: 1 if x == 'ACTIVE' else 0)
    
        # Splitting the dataset into train and test
        test_size, random_state = self.__configuration__.TEST_SIZE, self.__configuration__.RANDOM_STATE
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size, random_state=random_state)

        # Validating classifiers using 5-fold method
        log.info('Estimating best classifier...')
        best_classifier, best_mean_train_accuracy, best_mean_val_accuracy = None, 0, 0
        for classifier in get_predictor_classifiers(self.__configuration__):

            n_splits = self.__configuration__.KFOLD_N_SPLITS
            kf = KFold(n_splits=n_splits, shuffle=True, random_state=random_state)

            train_accuracies, val_accuracies = [], []
            for _, (train_indexes, val_indexes) in enumerate(kf.split(X_train)):

                # Extracting folded train and validation rows
                X_train_, y_train_ = X_train.iloc[train_indexes], y_train.iloc[train_indexes]
                X_val_, y_val_ = X_train.iloc[val_indexes], y_train.iloc[val_indexes]

                # Fitting model on folded dataset and getting train & validation accuracies
                class_weight = self.__configuration__.SAMPLE_WEIGHT_CLASS
                classifier.fit(X_train_, y_train_, compute_sample_weight(class_weight=class_weight, y=y_train_))
                train_accuracies.append(accuracy_score(y_train_, classifier.predict(X_train_)))
                val_accuracies.append(accuracy_score(y_val_, classifier.predict(X_val_)))

            mean_train_accuracy, mean_val_accuracy = np.mean(train_accuracies), np.mean(val_accuracies)
            log.info(f'Classifier: {classifier.type.name:<18} | Train Accuracy (Avg): {mean_train_accuracy:.2f} | Validation Accuracy (Avg): {mean_val_accuracy:.2f}')

            delta = abs(mean_val_accuracy - best_mean_val_accuracy)
            if (delta < 0.01 and mean_train_accuracy > best_mean_train_accuracy) or (mean_val_accuracy > best_mean_val_accuracy):
                best_classifier = classifier
                best_mean_train_accuracy = mean_train_accuracy
                best_mean_val_accuracy = mean_val_accuracy
            
        # Training & testing best classifier on complete dataset
        class_weight = self.__configuration__.SAMPLE_WEIGHT_CLASS
        best_classifier.fit(X_train, y_train, compute_sample_weight(class_weight=class_weight, y=y_train))
        y_pred, y_pred_probs = classifier.predict(X_test), classifier.predict_proba(X_test)[:, 1]
        test_accuracy = accuracy_score(y_test, y_pred)
        roc_auc = roc_auc_score(y_test, y_pred_probs)

        # Preparing metadata
        metadata = PredictorMetadata(self.__configuration__, best_classifier)
        metadata.cvs_files_count = csv_files_count
        metadata.rows_count = rows_count
        metadata.test_accuracy = test_accuracy
        metadata.roc_auc = roc_auc
        metadata.classification_report = classification_report(y_test, y_pred, zero_division=0)
        metadata.confusion_matrix = confusion_matrix(y_test, y_pred)

        # Logging metadata
        metadata.describe()    
        if self.__configuration__.show_plots:
            self.plot_roc_curve(y_test, y_pred_probs, roc_auc)

        # Exporting metadata
        metadata.export()

        # Exporting model's pickle file
        pickle_file_path = self.__configuration__.pickle_file_path
        best_classifier.export(pickle_file_path)
        log.info(f'Classifier exported: {pickle_file_path}')


    def plot_roc_curve(self, y_test, y_pred_probs, roc_auc) -> None:
        fpr, tpr, _ = roc_curve(y_test, y_pred_probs)
        plt.figure()
        plt.plot(fpr, tpr, label='ROC curve (area = %0.2f)' % roc_auc)
        plt.plot([0, 1], [0, 1], 'k--')
        plt.xlim([0.0, 1.0])
        plt.ylim([0.0, 1.05])
        plt.xlabel('False Positive Rate')
        plt.ylabel('True Positive Rate')
        plt.title('Receiver Operating Characteristic')
        plt.legend(loc='lower right')
        plt.show()

    
    def execute_model(self) -> None:
        classifier = PredictorClassifier.load(self.__configuration__.pickle_file_path)
        log.info(f'Loaded classifier: {classifier.type.name}')

        test_file_path = self.__configuration__.test_file_path
        test_file_dir = path.dirname(test_file_path)
        game_code = path.basename(test_file_path).split('.')[0]

        df = pd.read_csv(test_file_path)
        predictions = []
        for player_id, row in df.groupby(by='player.id'):
            X = self.extract_features(row)
            prediction = Prediction(game_code, 
                                    player_id, 
                                    classifier.predict_proba(X)[0].tolist(), 
                                    int(classifier.predict(X)[0]))
            predictions.append(prediction)

        # Finding test file directory
        prediction_file_path = path.join(test_file_dir, f'{game_code}.json')
        predictions_json = json.dumps([ prediction.__dict__ for prediction in predictions ], indent=4)
        with open(prediction_file_path, 'w') as file:
            file.write(predictions_json)
            log.info(f'Exported predctions to: {prediction_file_path}')


    def extract_features(self, df: pd.DataFrame) -> pd.DataFrame:
        features = df.copy()
        features['ownership.average'] = features['ownership.total'] / (features['ownership.count'] * 100)
        features['debit.invest.average'] = features['debit.invest.total'] / (features['debit.invest.count'] * df['player.base-cash'])
        features['debit.rent.average'] = features['debit.rent.total'] / (features['debit.rent.count'] * df['player.base-cash'])
        features['credit.rent.average'] = features['credit.rent.total'] / (features['credit.rent.count'] * df['player.base-cash'])
        for col in df.columns:
            if col.startswith('ownership') and not (col.endswith('total') or col.endswith('count')):
                features[col] /= 100
            elif (col.startswith('debit') or col.startswith('credit')) and not (col.endswith('total') or col.endswith('count')):
                features[col] /= df['player.base-cash']
            else:
                features = features.drop(col, axis=1)
        return features.fillna(0)
    

    def import_data(self) -> tuple[pd.DataFrame, int, int]:

        # Finding all the CSV files present in the data directory
        data_directory, csv_files = self.__configuration__.data_directory, []
        for export_file_path in listdir(data_directory):
            if export_file_path.endswith('.csv'):
                csv_files.append(path.join(data_directory, export_file_path))
        
        # Combining all the CSV files as one dataframe
        df = pd.concat([pd.read_csv(f) for f in csv_files], ignore_index=True)
        return df, len(csv_files), df.shape[0]
