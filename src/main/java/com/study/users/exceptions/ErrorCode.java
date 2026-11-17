package com.study.users.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_ERROR(400, "Проверьте поля"),
    UNAUTHORIZED(401, "Войдите в аккаунт"),
    WRONG_CREDENTIALS(401, "Неверный email или пароль"),
    FORBIDDEN(403, "Недостаточно прав"),
    NOT_OWN_PROFILE(403, "Можно менять только свой профиль"),
    ADMIN_ONLY(403, "Доступно только администратору"),
    NOT_FOUND(404, "Не найдено"),
    INTERNAL_ERROR(500, "Что-то пошло не так, попробуйте позже"),
    EMAIL_TAKEN(409, "Этот email уже занят"),
    USER_NOT_FOUND(404, "Пользователь не найден"),
    ROLE_NOT_FOUND(404, "Роль не найдена"),
    ROLE_ALREADY_ASSIGNED(409, "Эта роль уже есть"),
    ROLE_NOT_ASSIGNED(404, "У пользователя нет этой роли");

    private final int status;
    private final String message;
}
