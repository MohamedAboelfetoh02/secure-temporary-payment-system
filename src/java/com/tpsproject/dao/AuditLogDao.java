package com.tpsproject.dao;

import com.tpsproject.model.AuditLogEntry;
import com.tpsproject.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDao {

    private static final String INSERT_AUDIT_LOG =
            "INSERT INTO audit_logs (user_id, action_type, description, ip_address, target_type, target_id) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FIND_RECENT_AUDIT_LOGS =
            "SELECT al.id, al.user_id, u.username, al.action_type, al.description, al.ip_address, al.target_type, al.target_id, al.created_at "
            + "FROM audit_logs al "
            + "LEFT JOIN users u ON u.id = al.user_id "
            + "ORDER BY al.created_at DESC, al.id DESC LIMIT ?";

    public void log(Integer userId, String actionType, String description, String ipAddress,
            String targetType, Integer targetId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_AUDIT_LOG)) {

            bindAndExecute(statement, userId, actionType, description, ipAddress, targetType, targetId);
        }
    }

    public void log(Connection connection, Integer userId, String actionType, String description,
            String ipAddress, String targetType, Integer targetId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_AUDIT_LOG)) {
            bindAndExecute(statement, userId, actionType, description, ipAddress, targetType, targetId);
        }
    }

    private void bindAndExecute(PreparedStatement statement, Integer userId, String actionType,
            String description, String ipAddress, String targetType, Integer targetId) throws SQLException {

        if (userId == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setInt(1, userId);
        }

        statement.setString(2, actionType);
        statement.setString(3, description);
        statement.setString(4, ipAddress);
        statement.setString(5, targetType);

        if (targetId == null) {
            statement.setNull(6, Types.INTEGER);
        } else {
            statement.setInt(6, targetId);
        }

        statement.executeUpdate();
    }

    public List<AuditLogEntry> findRecentLogs(int limit) throws SQLException {
        List<AuditLogEntry> logs = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_RECENT_AUDIT_LOGS)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AuditLogEntry entry = new AuditLogEntry();
                    entry.setId(resultSet.getInt("id"));
                    int userId = resultSet.getInt("user_id");
                    entry.setUserId(resultSet.wasNull() ? null : userId);
                    entry.setUsername(resultSet.getString("username"));
                    entry.setActionType(resultSet.getString("action_type"));
                    entry.setDescription(resultSet.getString("description"));
                    entry.setIpAddress(resultSet.getString("ip_address"));
                    entry.setTargetType(resultSet.getString("target_type"));
                    int targetId = resultSet.getInt("target_id");
                    entry.setTargetId(resultSet.wasNull() ? null : targetId);
                    entry.setCreatedAt(resultSet.getTimestamp("created_at"));
                    logs.add(entry);
                }
            }
        }

        return logs;
    }
}
