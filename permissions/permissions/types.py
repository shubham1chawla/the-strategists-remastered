from enum import Enum

from pydantic import BaseModel


class GoogleRecaptchaVerificationRequest(BaseModel):
    client_token: str


class GoogleRecaptchaVerificationResponse(BaseModel):
    success: bool


class PermissionGroupRequest(BaseModel):
    email: str


class PermissionStatus(str, Enum):
    ENABLED = 'ENABLED'
    DISABLED = 'DISABLED'


class PermissionGroupResponse(BaseModel):
    email: str
    game_creation: PermissionStatus
