import pandas as pd
import pickle
from os import listdir, path
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.utils.class_weight import compute_sample_weight

MODEL_EXPORT_NAME = 'model.sav'

def export_model(data_dir: str, out_dir: str) -> None:
    csv_files = []
    for export_file_path in listdir(data_dir):
        if export_file_path.endswith('.csv'):
            csv_files.append(path.join(data_dir, export_file_path))
    print(f'CSV files found: {len(csv_files)}')

    all_games_data = pd.concat([pd.read_csv(f) for f in csv_files], ignore_index=True)
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
    input_df = pd.read_csv(predict_file)

    debit_columns = [col for col in input_df.columns if col.startswith('debit.')]
    credit_columns = [col for col in input_df.columns if col.startswith('credit.')]
    features = ['player.base-cash'] + debit_columns + credit_columns
    input_df_filtered = input_df[features]

    predicted_probability = model.predict_proba(input_df_filtered)[:, 1]

    threshold = 0.2
    prediction = (predicted_probability >= threshold).astype(int)

    print(f'Prediction: {prediction[0]}')