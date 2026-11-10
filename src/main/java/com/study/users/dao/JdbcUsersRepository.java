package com.study.users.dao;

import com.study.users.dto.Role;
import com.study.users.dto.User;
import com.study.users.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUsersRepository implements UsersRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DaoHelper daoHelper;

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(Constants.GET_USERS, (rs, rowNum) -> daoHelper.mapUser(rs));
    }

    @Override
    public Optional<User> findById(int id) {
        List<User> users = jdbcTemplate.query(Constants.GET_USER_BY_ID,
                (rs, rowNum) -> daoHelper.mapUser(rs), id);
        return users.stream().findFirst();
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
}
