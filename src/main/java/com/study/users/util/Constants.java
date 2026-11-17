package com.study.users.util;

public final class Constants {

    private Constants() {
    }

    private static final String USER_COLUMNS = """
            u.id, u.first_name, u.last_name, u.email, u.phone_number,
            ARRAY(SELECT r.name
                  FROM users_db.user_role ur
                  JOIN users_db.roles r ON r.id = ur.role_id
                  WHERE ur.user_id = u.id
                  ORDER BY r.level) AS roles,
            u.created_at, u.updated_at
            """;

    private static final String ROLE_FILTER = """
            WHERE (?::text IS NULL OR EXISTS (
                SELECT 1 FROM users_db.user_role ur
                JOIN users_db.roles r ON r.id = ur.role_id
                WHERE ur.user_id = u.id AND r.name = ?))
            """;

    public static final String GET_USERS = "SELECT " + USER_COLUMNS + """
            FROM users_db.users u
            """ + ROLE_FILTER + " ORDER BY u.id LIMIT ? OFFSET ?";

    public static final String COUNT_USERS = "SELECT count(*) FROM users_db.users u " + ROLE_FILTER;

    public static final String GET_USER_NAMES = """
            SELECT id, first_name, last_name FROM users_db.users
            WHERE id IN (:ids)
            ORDER BY id
            """;

    public static final String GET_USER_BY_ID = "SELECT " + USER_COLUMNS + """
            FROM users_db.users u
            WHERE u.id = ?
            """;

    public static final String USER_EXISTS = """
            SELECT EXISTS (SELECT 1 FROM users_db.users WHERE id = ?)
            """;

    public static final String GET_USER_BY_EMAIL = "SELECT " + USER_COLUMNS + """
            , u.password_hash FROM users_db.users u
            WHERE u.email = ?
            """;

    public static final String GET_USER_ROLES = """
            SELECT r.name
            FROM users_db.user_role ur
            JOIN users_db.roles r ON r.id = ur.role_id
            WHERE ur.user_id = ?
            ORDER BY r.level
            """;

    public static final String CREATE_USER = """
            INSERT INTO users_db.users (first_name, last_name, email, phone_number, password_hash)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, created_at, updated_at
            """;

    public static final String ADD_SUBSCRIBER_ROLE = """
            INSERT INTO users_db.user_role (user_id, role_id)
            SELECT ?, id FROM users_db.roles WHERE name = 'subscriber'
            """;

    public static final String GET_ROLES = """
            SELECT id, name, level
            FROM users_db.roles
            ORDER BY level
            """;

    public static final String UPDATE_USER = """
            UPDATE users_db.users
            SET first_name = CASE WHEN ? THEN ? ELSE first_name END,
                last_name = CASE WHEN ? THEN ? ELSE last_name END,
                phone_number = CASE WHEN ? THEN ? ELSE phone_number END,
                password_hash = CASE WHEN ? THEN ? ELSE password_hash END,
                updated_at = now()
            WHERE id = ?
            """;

    public static final String ROLE_EXISTS = """
            SELECT EXISTS (SELECT 1 FROM users_db.roles WHERE id = ?)
            """;

    public static final String ADD_USER_ROLE = """
            INSERT INTO users_db.user_role (user_id, role_id) VALUES (?, ?)
            """;

    public static final String REMOVE_USER_ROLE = """
            DELETE FROM users_db.user_role WHERE user_id = ? AND role_id = ?
            """;
}
