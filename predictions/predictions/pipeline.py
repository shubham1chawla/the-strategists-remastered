import pandas as pd
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.impute import SimpleImputer
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import FunctionTransformer


def create_ownership_average(x: pd.DataFrame) -> pd.DataFrame:
    x = x.copy()
    x["ownership.average"] = x["ownership.total"] / x["ownership.count"]
    return x


def create_debit_invest_average(x: pd.DataFrame) -> pd.DataFrame:
    x = x.copy()
    x["debit.invest.average"] = x["debit.invest.total"] / x["debit.invest.count"]
    return x


def create_debit_rent_average(x: pd.DataFrame) -> pd.DataFrame:
    x = x.copy()
    x["debit.rent.average"] = x["debit.rent.total"] / x["debit.rent.count"]
    return x


def create_credit_rent_average(x: pd.DataFrame) -> pd.DataFrame:
    x = x.copy()
    x["credit.rent.average"] = x["credit.rent.total"] / x["credit.rent.count"]
    return x


def is_property_ownership_feature(feature: str) -> bool:
    is_ownership = feature.startswith("ownership")
    is_total_or_count = feature.endswith("total") or feature.endswith("count")
    return is_ownership and not is_total_or_count


def scale_ownership_features(x: pd.DataFrame) -> pd.DataFrame:
    x = x.copy()
    features = [feature for feature in x.columns if is_property_ownership_feature(feature)]
    for feature in features:
        x[feature] = x[feature] / 100
    return x


def is_property_debit_credit_feature(feature: str) -> bool:
    is_debit_or_credit = feature.startswith("debit") or feature.startswith("credit")
    is_total_or_count = feature.endswith("total") or feature.endswith("count")
    return is_debit_or_credit and not is_total_or_count


def scale_debit_credit_features(x: pd.DataFrame) -> pd.DataFrame:
    x = x.copy()
    features = [feature for feature in x.columns if is_property_debit_credit_feature(feature)]
    for feature in features:
        x[feature] = x[feature] / x["player.base-cash"]
    return x


class FeatureSelector(BaseEstimator, TransformerMixin):

    def __init__(self, include_patterns=None, exclude_patterns=None):
        self.include_patterns = include_patterns or []
        self.exclude_patterns = exclude_patterns or []

    def fit(self, x, y=None):
        return self

    def transform(self, x):
        x = x.copy()
        features_to_keep = []

        for feature in x.columns:
            # Include columns that match include patterns
            include = False
            for pattern in self.include_patterns:
                if pattern in feature:
                    include = True
                    break

            # Exclude columns that match exclude patterns
            exclude = False
            for pattern in self.exclude_patterns:
                if pattern in feature:
                    exclude = True
                    break

            if include and not exclude:
                features_to_keep.append(feature)

        return x[features_to_keep]


def create_feature_engineering_pipeline() -> Pipeline:
    return Pipeline([

        # Adds average features
        ("create_avg_features", Pipeline([
            ("create_ownership_average", FunctionTransformer(create_ownership_average, validate=False)),
            ("create_debit_invest_average", FunctionTransformer(create_debit_invest_average, validate=False)),
            ("create_debit_rent_average", FunctionTransformer(create_debit_rent_average, validate=False)),
            ("create_credit_rent_average", FunctionTransformer(create_credit_rent_average, validate=False)),
        ])),

        # Scales features
        ("scale_features", Pipeline([
            ("scale_ownership_features", FunctionTransformer(scale_ownership_features, validate=False)),
            ("scale_debit_credit_features", FunctionTransformer(scale_debit_credit_features, validate=False)),
        ])),

        # Only keeping properties' ownership, credit, and debit-based features + averages
        ("select_features", FeatureSelector(
            include_patterns=["ownership", "debit", "credit"],
            exclude_patterns=["total", "count"],
        )),

        # Imputing missing values
        ("impute_missing", SimpleImputer(strategy="constant", fill_value=0.0).set_output(transform="pandas")),

    ])
