package com.study.users.services;

import com.study.users.dao.UsersRepository;
import com.study.users.dto.User;
import com.study.users.dto.UserRequest;
import com.study.users.exceptions.ApiException;
import com.study.users.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getUsers() {
        return usersRepository.findAll();
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
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        try {
            return usersRepository.createUser(user);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.EMAIL_TAKEN);
        }
    }
}
