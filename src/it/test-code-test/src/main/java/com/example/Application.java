package com.example;

import java.sql.SQLException;

public class Application {

    /**
     * <p>This code will allow the caller to provide an argument to the application which
     * finds its way into a SQL statement.  For example <code>';DROP TABLE product;SELECT '</code>
     * could be passed in and it would give an opportunity to attack the system at the database
     * level.</p>
     */

    public static void main(String[] args) {
        VulnerableQueryHelper vulnerableQueryHelper = new VulnerableQueryHelper(
                null // not relevant for the purpose of analysis
        );

        try {
            System.out.println("" + vulnerableQueryHelper.countProductOrders(args[1]));
        } catch (SQLException se) {
            throw new Error(se);
        }
    }

}
