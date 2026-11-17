package com.study.users.services;

import com.study.users.dao.UsersRepository;
import com.study.users.dto.Role;
import com.study.users.exceptions.ApiException;
import com.study.users.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UsersRepository usersRepository;

    public List<Role> getRoles() {
        return usersRepository.findRoles();
    }

    @Transactional
    public List<String> assignRole(int userId, int roleId) {
        checkUserAndRole(userId, roleId);
        try {
            usersRepository.addUserRole(userId, roleId);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.ROLE_ALREADY_ASSIGNED);
        }
        return usersRepository.findUserRoles(userId);
    }

    @Transactional
    public List<String> removeRole(int userId, int roleId) {
        checkUserAndRole(userId, roleId);
        if (!usersRepository.removeUserRole(userId, roleId)) {
            throw new ApiException(ErrorCode.ROLE_NOT_ASSIGNED);
        }
        return usersRepository.findUserRoles(userId);
    }

    private void checkUserAndRole(int userId, int roleId) {
        if (!usersRepository.existsById(userId)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        if (!usersRepository.roleExists(roleId)) {
            throw new ApiException(ErrorCode.ROLE_NOT_FOUND);
        }
    }
}
