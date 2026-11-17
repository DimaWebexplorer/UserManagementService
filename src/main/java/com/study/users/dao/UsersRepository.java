package com.study.users.dao;

import com.study.users.dto.Role;
import com.study.users.dto.User;
import com.study.users.dto.UpdateUserRequest;
import com.study.users.dto.UserName;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    List<User> findAll(String role, int limit, long offset);

    long countUsers(String role);

    List<UserName> findNamesByIds(List<Integer> ids);

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    boolean existsById(int id);

    List<String> findUserRoles(int userId);

    User createUser(User user);

    boolean updateUser(int id, UpdateUserRequest request, String passwordHash);

    List<Role> findRoles();

    boolean roleExists(int roleId);

    void addUserRole(int userId, int roleId);

    boolean removeUserRole(int userId, int roleId);
}
