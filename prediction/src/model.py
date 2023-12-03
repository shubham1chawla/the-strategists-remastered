import pickle
import pandas as pd
import matplotlib.pyplot as plt
from os import listdir, path
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.utils.class_weight import compute_sample_weight
from sklearn.metrics import classification_report, confusion_matrix, roc_auc_score, roc_curve

MODEL_EXPORT_NAME = 'model.sav'
PROBABILITY_THRESHOLD = 0.6
TEST_SPLIT_RATIO = 0.25
TEST_RANDOM_STATE = 42

def export_model(data_dir: str, out_dir: str) -> None:
    # Preparing CSV files
    csv_files = []
    for export_file_path in listdir(data_dir):
        if export_file_path.endswith('.csv'):
            csv_files.append(path.join(data_dir, export_file_path))
    print('CSV files found:', len(csv_files))

    # Feature extraction
    df = pd.concat([pd.read_csv(f) for f in csv_files], ignore_index=True)
    X = extract_features(df)
    y = df['player.state'].apply(lambda x: 1 if x == 'ACTIVE' else 0)
    print('Rows found:', len(df))

    # Sampling dataset
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=TEST_SPLIT_RATIO, random_state=TEST_RANDOM_STATE)
    sample_weights = compute_sample_weight(class_weight='balanced', y=y_train)

    # Training model
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

    # Scoring model
    print('Best parameters:', gs.best_params_)
    y_pred_gb = gs.best_estimator_.predict(X_test)
    y_pred_probs = gs.best_estimator_.predict_proba(X_test)[:, 1]
    # precision, recall, thresholds = precision_recall_curve(y_test, y_pred_probs)
    roc_auc = roc_auc_score(y_test, y_pred_probs)
    y_pred_adjusted = (y_pred_probs >= PROBABILITY_THRESHOLD).astype(int)

    # Evaluation metrics
    # plot_roc_curve(y_test, y_pred_probs, roc_auc)
    print('\nOriginal Classification Report:\n', classification_report(y_test, y_pred_gb, zero_division=0))
    print(f'Adjusted Classification Report (Threshold = {PROBABILITY_THRESHOLD}):\n', classification_report(y_test, y_pred_adjusted, zero_division=0))
    print('Confusion Matrix for Adjusted Predictions:\n', confusion_matrix(y_test, y_pred_adjusted))
    print('Area Under the ROC Curve: {:.2f}'.format(roc_auc))

    # exporting model
    export_file_path = path.join(out_dir, MODEL_EXPORT_NAME)
    pickle.dump(gs.best_estimator_, open(export_file_path, 'wb'))
    print('Exported:', export_file_path)


def plot_roc_curve(y_test, y_pred_probs, roc_auc) -> None:
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


def evaluate_model(model_dir: str, predict_file: str) -> None:
    model_file_path = path.join(model_dir, MODEL_EXPORT_NAME)
    print('Model:', model_file_path)
    print('File:', predict_file)

    model = pickle.load(open(model_file_path, 'rb'))
    df = pd.read_csv(predict_file)
    df = extract_features(df)

    predicted_probability = model.predict_proba(df)[:, 1]
    prediction = (predicted_probability >= PROBABILITY_THRESHOLD).astype(int)
    print('Prediction:', prediction[0])


def extract_features(df: pd.DataFrame) -> pd.DataFrame:
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
