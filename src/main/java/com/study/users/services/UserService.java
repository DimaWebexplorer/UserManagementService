package com.study.users.services;

import com.study.users.dao.UsersRepository;
import com.study.users.dto.User;
import com.study.users.dto.UserRequest;
import com.study.users.dto.LoginRequest;
import com.study.users.dto.LoginResponse;
import com.study.users.dto.UpdateUserRequest;
import com.study.users.dto.UserName;
import com.study.users.dto.UserPage;
import com.study.users.exceptions.ApiException;
import com.study.users.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public UserPage getUsers(String role, int page, int limit) {
        long offset = ((long) page - 1) * limit;
        return new UserPage(usersRepository.findAll(role, limit, offset),
                usersRepository.countUsers(role), page, limit);
    }

    public List<UserName> getUserNames(List<Integer> ids) {
        return usersRepository.findNamesByIds(ids);
    }

    public User getUser(int id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    public List<String> getUserRoles(int id) {
        if (!usersRepository.existsById(id)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        return usersRepository.findUserRoles(id);
    }

    public User register(UserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(normalizeEmail(request.getEmail()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        try {
            return usersRepository.createUser(user);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.EMAIL_TAKEN);
        }
    }

    public LoginResponse login(LoginRequest request) {
        User user = usersRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(ErrorCode.WRONG_CREDENTIALS);
        }
        return new LoginResponse(tokenService.createToken(user), user);
    }

    @Transactional
    public User updateUser(int id, UpdateUserRequest request) {
        String passwordHash = request.hasField("password") ? passwordEncoder.encode(request.getPassword()) : null;
        if (!usersRepository.updateUser(id, request, passwordHash)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        return getUser(id);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
