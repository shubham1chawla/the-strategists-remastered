from os import listdir, access, W_OK
from os.path import exists, isdir

"""
This file contains the class to train the model.
"""

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
        for i, filename in enumerate(listdir(self.__data_directory__)):
            print(f'{i} - {filename}')
