from enum import Enum

"""
This file contains different modes for the Analysis Model.
"""

class Mode(Enum):
    TRAIN = 'train'
    PREDICT = 'predict'

    def __str__(self):
        return self.value