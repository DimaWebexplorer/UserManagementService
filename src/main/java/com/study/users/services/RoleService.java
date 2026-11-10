package com.study.users.services;

import com.study.users.dao.UsersRepository;
import com.study.users.dto.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UsersRepository usersRepository;

    public List<Role> getRoles() {
        return usersRepository.findRoles();
    }
}
