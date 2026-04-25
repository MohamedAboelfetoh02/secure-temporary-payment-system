package com.tpsproject.dao;

import com.tpsproject.model.CurrencyPackage;
import com.tpsproject.model.Game;
import com.tpsproject.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameDao {

    private static final String FIND_ACTIVE_GAMES_WITH_PACKAGES =
            "SELECT g.id AS game_id, g.title, g.description, g.genre, g.status AS game_status, "
            + "cp.id AS package_id, cp.package_name, cp.currency_name, cp.currency_amount, cp.price, cp.status AS package_status "
            + "FROM games g "
            + "LEFT JOIN currency_packages cp ON cp.game_id = g.id AND cp.status = 'ACTIVE' "
            + "WHERE g.status = 'ACTIVE' "
            + "ORDER BY g.title, cp.price";

    private static final String COUNT_ACTIVE_GAMES =
            "SELECT COUNT(*) FROM games WHERE status = 'ACTIVE'";

    private static final String COUNT_ACTIVE_PACKAGES =
            "SELECT COUNT(*) FROM currency_packages WHERE status = 'ACTIVE'";

    public List<Game> findActiveGamesWithPackages() throws SQLException {
        Map<Integer, Game> gameMap = new LinkedHashMap<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_GAMES_WITH_PACKAGES);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int gameId = resultSet.getInt("game_id");
                Game game = gameMap.get(gameId);

                if (game == null) {
                    game = new Game();
                    game.setId(gameId);
                    game.setTitle(resultSet.getString("title"));
                    game.setDescription(resultSet.getString("description"));
                    game.setGenre(resultSet.getString("genre"));
                    game.setStatus(resultSet.getString("game_status"));
                    gameMap.put(gameId, game);
                }

                int packageId = resultSet.getInt("package_id");
                if (!resultSet.wasNull()) {
                    CurrencyPackage currencyPackage = new CurrencyPackage();
                    currencyPackage.setId(packageId);
                    currencyPackage.setGameId(gameId);
                    currencyPackage.setPackageName(resultSet.getString("package_name"));
                    currencyPackage.setCurrencyName(resultSet.getString("currency_name"));
                    currencyPackage.setCurrencyAmount(resultSet.getInt("currency_amount"));
                    currencyPackage.setPrice(resultSet.getBigDecimal("price"));
                    currencyPackage.setStatus(resultSet.getString("package_status"));
                    game.getCurrencyPackages().add(currencyPackage);
                }
            }
        }

        return new ArrayList<>(gameMap.values());
    }

    public int countActiveGames() throws SQLException {
        return countBySql(COUNT_ACTIVE_GAMES);
    }

    public int countActivePackages() throws SQLException {
        return countBySql(COUNT_ACTIVE_PACKAGES);
    }

    private int countBySql(String sql) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }

        return 0;
    }
}
