package com.api.demo.service;

import org.springframework.stereotype.Service;

import com.api.demo.util.ExternalPropertyUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Service
public class ExternalDBService {

    private static final int MAX_RETRY = 7;
    private static final String CHECK_SQL = "SELECT 1 FROM DUAL";

    public Connection getDBConnection() throws Exception {

        String username = ExternalPropertyUtil.get("DbUsername");
        String password = ExternalPropertyUtil.get("DbPassword");
        String url = ExternalPropertyUtil.get("DBURL");

        int retry = 0;

        while (retry < MAX_RETRY) {
            try {
                Connection con = DriverManager.getConnection(url, username, password);

                try (Statement stmt = con.createStatement()) {
                    stmt.executeQuery(CHECK_SQL);
                }

                return con; // ✅ SUCCESS

            } catch (Exception ex) {
                retry++;
                System.out.println("Retry " + retry + " failed: " + ex.getMessage());
                Thread.sleep(1000);
            }
        }

        throw new RuntimeException("Unable to connect to DB after retries");
    }

    public void release(Connection con) {
        try {
            if (con != null) con.close();
        } catch (Exception ignored) {
        }
    }
}
