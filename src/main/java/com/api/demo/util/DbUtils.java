package com.api.demo.util;

import java.sql.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbUtils {

    private DbUtils() {
        // Utility class, prevent instantiation
    }

    /* ===================== CLOSE METHODS ===================== */

    /**
     * Close Connection
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.warn("Error closing Connection: {}", e.getMessage());
            }
        }
    }

    /**
     * Close Statement
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.warn("Error closing Statement: {}", e.getMessage());
            }
        }
    }

    /**
     * Close PreparedStatement
     */
    public static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                log.warn("Error closing PreparedStatement: {}", e.getMessage());
            }
        }
    }

    /**
     * Close ResultSet
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.warn("Error closing ResultSet: {}", e.getMessage());
            }
        }
    }

    /* ===================== CLOSE ALL METHODS ===================== */

    /**
     * Close all resources (Statement version)
     */
    public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
        close(rs);
        close(stmt);
        close(conn);
    }

    /**
     * Close all resources (PreparedStatement version)
     */
    public static void closeAll(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        close(rs);
        close(pstmt);
        close(conn);
    }

    /**
     * Close all resources with multiple statements
     */
    public static void closeAll(ResultSet rs, Statement stmt1, Statement stmt2, Connection conn) {
        close(rs);
        close(stmt1);
        close(stmt2);
        close(conn);
    }

    /**
     * Close all resources with multiple PreparedStatements
     */
    public static void closeAll(ResultSet rs, PreparedStatement pstmt1, PreparedStatement pstmt2, Connection conn) {
        close(rs);
        close(pstmt1);
        close(pstmt2);
        close(conn);
    }

    /**
     * Close all resources with multiple statements (array version)
     */
    public static void closeAll(ResultSet rs, Connection conn, Statement... statements) {
        close(rs);
        for (Statement stmt : statements) {
            close(stmt);
        }
        close(conn);
    }

    /**
     * Close all resources with multiple PreparedStatements (array version)
     */
    public static void closeAll(ResultSet rs, Connection conn, PreparedStatement... pstatements) {
        close(rs);
        for (PreparedStatement pstmt : pstatements) {
            close(pstmt);
        }
        close(conn);
    }

    /* ===================== TRANSACTION METHODS ===================== */

    /**
     * Rollback transaction
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.warn("Error during rollback: {}", e.getMessage());
            }
        }
    }

    /**
     * Commit transaction
     */
    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                log.warn("Error during commit: {}", e.getMessage());
            }
        }
    }

    /**
     * Set auto-commit
     */
    public static void setAutoCommit(Connection conn, boolean autoCommit) {
        if (conn != null) {
            try {
                conn.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                log.warn("Error setting auto-commit: {}", e.getMessage());
            }
        }
    }

    /**
     * Check if connection is closed
     */
    public static boolean isClosed(Connection conn) {
        if (conn != null) {
            try {
                return conn.isClosed();
            } catch (SQLException e) {
                log.warn("Error checking connection status: {}", e.getMessage());
                return true;
            }
        }
        return true;
    }

    /**
     * Check if result set is closed
     */
    public static boolean isClosed(ResultSet rs) {
        if (rs != null) {
            try {
                return rs.isClosed();
            } catch (SQLException e) {
                log.warn("Error checking result set status: {}", e.getMessage());
                return true;
            }
        }
        return true;
    }

    /* ===================== HELPER METHODS ===================== */

    /**
     * Safely set null parameter
     */
    public static void setNull(PreparedStatement pstmt, int parameterIndex, int sqlType) throws SQLException {
        pstmt.setNull(parameterIndex, sqlType);
    }

    /**
     * Safely set string parameter (handle null)
     */
    public static void setString(PreparedStatement pstmt, int parameterIndex, String value) throws SQLException {
        if (value != null) {
            pstmt.setString(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, Types.VARCHAR);
        }
    }

    /**
     * Safely set long parameter (handle null)
     */
    public static void setLong(PreparedStatement pstmt, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            pstmt.setLong(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, Types.BIGINT);
        }
    }

    /**
     * Safely set integer parameter (handle null)
     */
    public static void setInt(PreparedStatement pstmt, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            pstmt.setInt(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, Types.INTEGER);
        }
    }

    /**
     * Safely set date parameter (handle null)
     */
    public static void setDate(PreparedStatement pstmt, int parameterIndex, java.util.Date value) throws SQLException {
        if (value != null) {
            pstmt.setDate(parameterIndex, new java.sql.Date(value.getTime()));
        } else {
            pstmt.setNull(parameterIndex, Types.DATE);
        }
    }

    /**
     * Safely set timestamp parameter (handle null)
     */
    public static void setTimestamp(PreparedStatement pstmt, int parameterIndex, Timestamp value) throws SQLException {
        if (value != null) {
            pstmt.setTimestamp(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, Types.TIMESTAMP);
        }
    }
}