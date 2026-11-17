package com.study.users.dao;

import com.study.users.dto.Role;
import com.study.users.dto.User;
import com.study.users.dto.UpdateUserRequest;
import com.study.users.dto.UserName;
import com.study.users.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUsersRepository implements UsersRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final DaoHelper daoHelper;

    @Override
    public List<User> findAll(String role, int limit, long offset) {
        return jdbcTemplate.query(Constants.GET_USERS, (rs, rowNum) -> daoHelper.mapUser(rs),
                role, role, limit, offset);
    }

    @Override
    public long countUsers(String role) {
        return jdbcTemplate.queryForObject(Constants.COUNT_USERS, Long.class, role, role);
    }

    @Override
    public List<UserName> findNamesByIds(List<Integer> ids) {
        return namedJdbcTemplate.query(Constants.GET_USER_NAMES, Map.of("ids", ids),
                (rs, rowNum) -> new UserName(rs.getInt("id"), rs.getString("first_name"),
                        rs.getString("last_name")));
    }

    @Override
    public Optional<User> findById(int id) {
        List<User> users = jdbcTemplate.query(Constants.GET_USER_BY_ID,
                (rs, rowNum) -> daoHelper.mapUser(rs), id);
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query(Constants.GET_USER_BY_EMAIL, (rs, rowNum) -> {
            User user = daoHelper.mapUser(rs);
            user.setPasswordHash(rs.getString("password_hash"));
            return user;
        }, email).stream().findFirst();
    }

    @Override
    public boolean existsById(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(Constants.USER_EXISTS, Boolean.class, id));
    }

    @Override
    public List<String> findUserRoles(int userId) {
        return jdbcTemplate.queryForList(Constants.GET_USER_ROLES, String.class, userId);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        jdbcTemplate.queryForObject(Constants.CREATE_USER, (rs, rowNum) -> {
                    user.setId(rs.getInt("id"));
                    user.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                    user.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
                    return user;
                },
                user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getPasswordHash());

        jdbcTemplate.update(Constants.ADD_SUBSCRIBER_ROLE, user.getId());
        user.setRoles(List.of("subscriber"));
        return user;
    }

    @Override
    public List<Role> findRoles() {
        return jdbcTemplate.query(Constants.GET_ROLES, (rs, rowNum) -> daoHelper.mapRole(rs));
    }

    @Override
    public boolean updateUser(int id, UpdateUserRequest request, String passwordHash) {
        return jdbcTemplate.update(Constants.UPDATE_USER,
                request.hasField("first_name"), trim(request.getFirstName()),
                request.hasField("last_name"), trim(request.getLastName()),
                request.hasField("phone_number"), request.getPhoneNumber(),
                request.hasField("password"), passwordHash, id) > 0;
    }

    @Override
    public boolean roleExists(int roleId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(Constants.ROLE_EXISTS, Boolean.class, roleId));
    }

    @Override
    public void addUserRole(int userId, int roleId) {
        jdbcTemplate.update(Constants.ADD_USER_ROLE, userId, roleId);
    }

    @Override
    public boolean removeUserRole(int userId, int roleId) {
        return jdbcTemplate.update(Constants.REMOVE_USER_ROLE, userId, roleId) > 0;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
