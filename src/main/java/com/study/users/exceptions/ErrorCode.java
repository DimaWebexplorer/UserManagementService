package com.study.users.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_ERROR(400, "Проверьте поля"),
    NOT_FOUND(404, "Не найдено"),
    INTERNAL_ERROR(500, "Что-то пошло не так, попробуйте позже"),
    EMAIL_TAKEN(409, "Этот email уже занят"),
    USER_NOT_FOUND(404, "Пользователь не найден");

    private final int status;
    private final String message;
}
