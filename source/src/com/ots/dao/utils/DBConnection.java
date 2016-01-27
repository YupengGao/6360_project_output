package com.ots.dao.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	private static Connection conn = null;

	public static Connection getConn() {
		try {
			if (conn == null) {
				Class.forName(Config.CLASS_NAME);
				conn = DriverManager.getConnection(Config.DATABASE_URL_ENTIRE,
						Config.USERNAME, Config.PASSWORD);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	public static void closeConn(ResultSet rs, PreparedStatement pstmt, boolean isCloseConn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		if (isCloseConn && conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void closeConn(PreparedStatement pstmt) {
		closeConn(null, pstmt, true);
	}
	
	public static void closeConn() {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}
