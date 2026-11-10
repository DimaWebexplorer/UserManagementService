package com.study.users.validators;

import com.study.users.dto.UserRequest;
import com.study.users.exceptions.ApiException;
import com.study.users.exceptions.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
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

        String email = body.getEmail();
        if (email == null || email.isBlank()) {
            errors.put("email", "Обязательное поле");
        } else if (email.length() > MAX_LENGTH || !EMAIL.matcher(email.trim()).matches()) {
            errors.put("email", "Некорректный email");
        }

        String phone = body.getPhoneNumber();
        if (phone != null && !PHONE.matcher(phone).matches()) {
            errors.put("phone_number", "Телефон в формате +79001112233");
        }

        String password = body.getPassword();
        if (password == null || password.length() < 4 || password.length() > 72) {
            errors.put("password", "Пароль от 4 до 72 символов");
        }

        if (!errors.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
        }
    }

    private void checkName(Map<String, String> errors, String field, String value) {
        if (value == null || value.isBlank()) {
            errors.put(field, "Обязательное поле");
        } else if (value.length() > MAX_LENGTH) {
            errors.put(field, "Слишком длинное");
        }
    }
}
