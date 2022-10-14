package com.example;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VulnerableQueryHelper {

    private final DataSource dataSource;

    public VulnerableQueryHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int countProductOrders(String productCode) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return countProductOrders(connection, productCode);
        }
    }

    /**
     * <p>In this method, the argument provided is put directly into the SQL which would
     * allow an attacker to be able to execute arbitrary logic in the database.</p>
     */

    public int countProductOrders(Connection connection, String productCode) throws SQLException {
        String sql = "SELECT COUNT(id)\n"
                + "FROM order o JOIN product p ON o.product_id = o.id\n"
                + "WHERE p.code = '" + productCode + "'";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException("expected a row to be returned from the sql query");
                }
                return resultSet.getInt(1);
            }
        }
    }

}
