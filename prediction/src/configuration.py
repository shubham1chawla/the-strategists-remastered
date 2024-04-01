# Standard Python imports
from typing import Final, final, Any
from dataclasses import dataclass


@final
@dataclass
class PredictorConfiguration:

    TEST_SIZE: Final[float] = 0.25
    RANDOM_STATE: Final[int] = 42
    CROSS_VALIDATOR: Final[int] = 5
    SCORING: Final[str] = 'f1'
    GS_N_JOBS: Final[int] = 8
    KFOLD_N_SPLITS: Final[int] = 4
    SAMPLE_WEIGHT_CLASS: Final[str] = 'balanced'

    data_directory: str = ''
    metadata_directory: str = ''
    pickle_file_path: str = ''
    test_file_path: str = ''
    show_plots: bool = False


    @staticmethod
    def fromArgs(args: Any) -> 'PredictorConfiguration':
        configuration = PredictorConfiguration()
        configuration.data_directory = args.data_dir if hasattr(args, 'data_dir') else configuration.data_directory
        configuration.metadata_directory = args.meta_dir if hasattr(args, 'meta_dir') else configuration.metadata_directory
        configuration.pickle_file_path = args.pickle_path if hasattr(args, 'pickle_path') else configuration.pickle_file_path
        configuration.test_file_path = args.test_csv if hasattr(args, 'test_csv') else configuration.test_file_path
        configuration.show_plots = args.plots if hasattr(args, 'plots') else configuration.show_plots
        return configuration
