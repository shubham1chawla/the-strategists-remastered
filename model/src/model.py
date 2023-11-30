import pandas as pd
import pickle
from os import listdir, path
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.utils.class_weight import compute_sample_weight

MODEL_EXPORT_NAME = 'model.sav'
PROBABILITY_THRESHOLD = 0.2
TEST_SPLIT_RATIO = 0.25
TEST_RANDOM_STATE = 42

def export_model(data_dir: str, out_dir: str) -> None:
    csv_files = []
    for export_file_path in listdir(data_dir):
        if export_file_path.endswith('.csv'):
            csv_files.append(path.join(data_dir, export_file_path))
    print(f'CSV files found: {len(csv_files)}')

    df = pd.concat([pd.read_csv(f) for f in csv_files], ignore_index=True)
    X = df[get_features(df)]
    y = df['player.state'].apply(lambda x: 1 if x == 'ACTIVE' else 0)

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=TEST_SPLIT_RATIO, random_state=TEST_RANDOM_STATE)

    sample_weights = compute_sample_weight(class_weight='balanced', y=y_train)

    param_grid = {
        'n_estimators': [50, 100, 200],
        'max_depth': [3, 5, 10],
        'min_samples_leaf': [1, 2, 4]
    }

    gs = GridSearchCV(estimator=GradientBoostingClassifier(random_state=42),
                                param_grid=param_grid,
                                scoring='f1',
                                cv=5)

    gs.fit(X_train, y_train, sample_weight=sample_weights)

    export_file_path = path.join(out_dir, MODEL_EXPORT_NAME)
    pickle.dump(gs.best_estimator_, open(export_file_path, 'wb'))
    print(f'Exported: {export_file_path}')


def evaluate_model(model_dir: str, predict_file: str) -> None:
    model_file_path = path.join(model_dir, MODEL_EXPORT_NAME)
    print(f'Model: {model_file_path}')

    model = pickle.load(open(model_file_path, 'rb'))
    df = pd.read_csv(predict_file)
    df = df[get_features(df)]

    predicted_probability = model.predict_proba(df)[:, 1]
    prediction = (predicted_probability >= PROBABILITY_THRESHOLD).astype(int)
    print(f'Prediction: {prediction[0]}')


def get_features(df: pd.DataFrame) -> pd.DataFrame:
    ownership_columns = [col for col in df.columns if col.startswith('ownership.')]
    debit_columns = [col for col in df.columns if col.startswith('debit.')]
    credit_columns = [col for col in df.columns if col.startswith('credit.')]
    return ['player.base-cash'] + debit_columns + credit_columns + ownership_columns
