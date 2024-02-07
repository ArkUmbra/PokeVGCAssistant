package com.arkumbra;

//import org.neo4j.driver.internal.spi.Connection;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    private void connect() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost");

        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("MATCH (n:User) RETURN n.name");
            while (rs.next()) {
                System.out.println(rs.getString("n.name"));
            }
        }
        con.close();
    }
}