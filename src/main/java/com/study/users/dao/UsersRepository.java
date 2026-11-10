package com.study.users.dao;

import com.study.users.dto.Role;
import com.study.users.dto.User;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    List<User> findAll();

    Optional<User> findById(int id);

    boolean existsById(int id);

    List<String> findUserRoles(int userId);

    User createUser(User user);

    List<Role> findRoles();
}
