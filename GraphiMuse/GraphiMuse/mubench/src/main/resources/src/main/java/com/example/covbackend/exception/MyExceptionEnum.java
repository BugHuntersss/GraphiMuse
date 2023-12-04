package com.example.covbackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum MyExceptionEnum {
    UNKNOWN_ERROR,
    DEFAULT_ERROR,
    SQL_ERROR,
    LOGIN_ERROR;
}
