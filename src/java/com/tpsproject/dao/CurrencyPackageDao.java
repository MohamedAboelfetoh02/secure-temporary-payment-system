package com.tpsproject.dao;

import com.tpsproject.model.CurrencyPackage;
import com.tpsproject.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyPackageDao {

    private static final String FIND_ACTIVE_PACKAGE_BY_ID =
            "SELECT cp.id, cp.game_id, cp.package_name, cp.currency_name, cp.currency_amount, cp.price, cp.status, "
            + "g.title AS game_title, g.description AS game_description, g.genre AS game_genre "
            + "FROM currency_packages cp "
            + "JOIN games g ON g.id = cp.game_id "
            + "WHERE cp.id = ? AND cp.status = 'ACTIVE' AND g.status = 'ACTIVE'";

    public CurrencyPackage findActiveById(int packageId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection()) {
            return findActiveById(connection, packageId);
        }
    }

    public CurrencyPackage findActiveById(Connection connection, int packageId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_PACKAGE_BY_ID)) {
            statement.setInt(1, packageId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    CurrencyPackage currencyPackage = new CurrencyPackage();
                    currencyPackage.setId(resultSet.getInt("id"));
                    currencyPackage.setGameId(resultSet.getInt("game_id"));
                    currencyPackage.setGameTitle(resultSet.getString("game_title"));
                    currencyPackage.setGameDescription(resultSet.getString("game_description"));
                    currencyPackage.setGameGenre(resultSet.getString("game_genre"));
                    currencyPackage.setPackageName(resultSet.getString("package_name"));
                    currencyPackage.setCurrencyName(resultSet.getString("currency_name"));
                    currencyPackage.setCurrencyAmount(resultSet.getInt("currency_amount"));
                    currencyPackage.setPrice(resultSet.getBigDecimal("price"));
                    currencyPackage.setStatus(resultSet.getString("status"));
                    return currencyPackage;
                }
            }
        }

        return null;
    }
}
