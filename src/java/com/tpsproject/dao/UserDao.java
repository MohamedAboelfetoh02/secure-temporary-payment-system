package com.tpsproject.dao;

import com.tpsproject.model.User;
import com.tpsproject.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDao {

    private static final String FIND_BY_IDENTIFIER =
            "SELECT id, username, email, password_hash, role, status, created_at, updated_at, last_login_at "
            + "FROM users WHERE username = ? OR email = ? LIMIT 1";

    private static final String CHECK_USERNAME =
            "SELECT COUNT(*) FROM users WHERE username = ?";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM users WHERE email = ?";

    private static final String INSERT_PLAYER =
            "INSERT INTO users (username, email, password_hash, role, status) VALUES (?, ?, ?, 'PLAYER', 'ACTIVE')";

    private static final String UPDATE_LAST_LOGIN =
            "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String COUNT_USERS_BY_ROLE =
            "SELECT COUNT(*) FROM users WHERE role = ? AND status = 'ACTIVE'";

    public User findByUsernameOrEmail(String identifier) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_IDENTIFIER)) {

            statement.setString(1, identifier);
            statement.setString(2, identifier);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    public boolean usernameExists(String username) throws SQLException {
        return recordExists(CHECK_USERNAME, username);
    }

    public boolean emailExists(String email) throws SQLException {
        return recordExists(CHECK_EMAIL, email);
    }

    public int createPlayer(User user) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("User account could not be created.");
    }

    public void updateLastLogin(int userId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_LAST_LOGIN)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public int countActiveUsersByRole(String role) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(COUNT_USERS_BY_ROLE)) {
            statement.setString(1, role);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    private boolean recordExists(String sql, String value) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setStatus(resultSet.getString("status"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        user.setLastLoginAt(resultSet.getTimestamp("last_login_at"));
        return user;
    }
}
