package com.study.users.validators;

import com.study.users.dto.UserRequest;
import com.study.users.dto.LoginRequest;
import com.study.users.dto.UpdateUserRequest;
import com.study.users.exceptions.ApiException;
import com.study.users.exceptions.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
public class RequestValidator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?\\d{10,15}$");
    private static final int MAX_LENGTH = 255;

    public void validateRegister(UserRequest body) {
        Map<String, String> errors = new LinkedHashMap<>();
        checkName(errors, "first_name", body.getFirstName());
        checkName(errors, "last_name", body.getLastName());

        checkEmail(errors, body.getEmail());
        checkPhone(errors, body.getPhoneNumber());
        checkPassword(errors, body.getPassword());
        throwIfInvalid(errors);
    }

    public void validateLogin(LoginRequest body) {
        Map<String, String> errors = new LinkedHashMap<>();
        checkEmail(errors, body.getEmail());
        checkPassword(errors, body.getPassword());
        throwIfInvalid(errors);
    }

    public void validateUpdate(UpdateUserRequest body) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (body.getFields().isEmpty()) {
            errors.put("body", "Укажите хотя бы одно поле профиля");
        }
        if (body.hasField("first_name")) {
            checkName(errors, "first_name", body.getFirstName());
        }
        if (body.hasField("last_name")) {
            checkName(errors, "last_name", body.getLastName());
        }
        if (body.hasField("phone_number")) {
            checkPhone(errors, body.getPhoneNumber());
        }
        if (body.hasField("password")) {
            checkPassword(errors, body.getPassword());
        }
        throwIfInvalid(errors);
    }

    public void validateId(Integer id, String field) {
        if (id == null || id <= 0) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, Map.of(field, "Нужен положительный id"));
        }
    }

    public void validateOwnProfile(int id, int currentUserId) {
        if (id != currentUserId) {
            throw new ApiException(ErrorCode.NOT_OWN_PROFILE);
        }
    }

    public void validatePage(String role, int page, int limit) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (role != null && !Set.of("subscriber", "producer", "administrator").contains(role)) {
            errors.put("role", "Неизвестная роль");
        }
        if (page < 1) {
            errors.put("page", "Номер страницы от 1");
        }
        if (limit < 1 || limit > 100) {
            errors.put("limit", "От 1 до 100 пользователей");
        }
        throwIfInvalid(errors);
    }

    public List<Integer> parseIds(String value, Map<String, String> parameters) {
        if (parameters.containsKey("role") || parameters.containsKey("page") || parameters.containsKey("limit")) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR,
                    Map.of("ids", "Не совмещайте ids с role, page или limit"));
        }
        String[] parts = value.split(",", -1);
        if (parts.length > 100) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, Map.of("ids", "Не больше 100 id"));
        }
        List<Integer> ids = new ArrayList<>();
        for (String part : parts) {
            try {
                int id = Integer.parseInt(part.trim());
                if (id <= 0) {
                    throw new NumberFormatException();
                }
                ids.add(id);
            } catch (NumberFormatException e) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR,
                        Map.of("ids", "Нужны положительные id через запятую"));
            }
        }
        return ids.stream().distinct().toList();
    }

    private void checkEmail(Map<String, String> errors, String email) {
        if (email == null || email.isBlank()) {
            errors.put("email", "Обязательное поле");
        } else if (email.trim().length() > MAX_LENGTH || !EMAIL.matcher(email.trim()).matches()) {
            errors.put("email", "Некорректный email");
        }
    }

    private void checkPhone(Map<String, String> errors, String phone) {
        if (phone != null && !PHONE.matcher(phone).matches()) {
            errors.put("phone_number", "Телефон в формате +79001112233");
        }
    }

    private void checkPassword(Map<String, String> errors, String password) {
        if (password == null || password.length() < 4 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            errors.put("password", "Пароль от 4 символов и не больше 72 байт UTF-8");
        }
    }

    private void throwIfInvalid(Map<String, String> errors) {
        if (!errors.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
        }
    }

    private void checkName(Map<String, String> errors, String field, String value) {
        if (value == null || value.isBlank()) {
            errors.put(field, "Обязательное поле");
        } else if (value.trim().length() > MAX_LENGTH) {
            errors.put(field, "Слишком длинное");
        }
    }
}
