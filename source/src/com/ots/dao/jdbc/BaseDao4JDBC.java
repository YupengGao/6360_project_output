package com.ots.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ots.dao.utils.DBConnection;
import com.ots.dao.utils.ObjectMapper;

public class BaseDao4JDBC {
	/**
	 * 
	 * @param sql
	 * @param objs
	 * @param mapper
	 * @return
	 */
	public static Object find(final String sql, final Object[] objs,
			final ObjectMapper mapper) {
		Object object = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConn();
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				pstmt.setObject(i + 1, objs[i]);
			}
			rs = pstmt.executeQuery();
			if (rs.next()) {
				object = mapper.mapping(rs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DBConnection.closeConn(rs, pstmt, true);
		}
		return object;
	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param mapper
	 * @return
	 */
	public static List<? extends Object> query(final String sql,
			final Object[] objs, final ObjectMapper mapper) {
		Object object = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final List<Object> list = new ArrayList<Object>();
		try {
			conn = DBConnection.getConn();
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				pstmt.setObject(i + 1, objs[i]);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				object = mapper.mapping(rs);
				list.add(object);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DBConnection.closeConn(rs, pstmt, true);
		}
		return list;
	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param isGenerateKey
	 * @return
	 */
	public static boolean update(final String sql, final Object[] objs,
			final boolean isGenerateKey) {
		return BaseDao4JDBC.update(sql, objs, isGenerateKey, false);

	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param isGenerateKey
	 * @return
	 */
	public static boolean update(final String sql, final Object[] objs,
			final boolean isGenerateKey, final boolean isCommit) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean bFlag = false;
		try {
			conn = DBConnection.getConn();
			pstmt = isGenerateKey ? conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS) : conn
					.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				pstmt.setObject(i + 1, objs[i]);
			}
			int i = pstmt.executeUpdate();
			if (i > 0) {
				bFlag = true;
			}
			if (isCommit) {
				conn.commit();
			}
		} catch (SQLException ex) {
			if (isCommit) {
				try {
					conn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			ex.printStackTrace();
		} finally {
			DBConnection.closeConn(pstmt);
		}
		return bFlag;
	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param isGenerateKey
	 * @return
	 */
	public static boolean batchUpdate(final String[] sqls,
			final boolean isCommit) {
		Connection conn = null;
		Statement pstmt = null;
		boolean bFlag = false;
		try {
			conn = DBConnection.getConn();
			pstmt = conn.createStatement();
			for (String sql : sqls) {
				pstmt.addBatch(sql);
			}
			pstmt.executeBatch();
			if (isCommit) {
				conn.commit();
			}
		} catch (SQLException ex) {
			if (isCommit) {
				try {
					conn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			ex.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			DBConnection.closeConn();
		}
		return bFlag;
	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param isGenerateKey
	 * @return
	 */
	public static Long insertReturnGeneratedKey(final String sql,
			final Object[] objs, final boolean isGenerateKey) {
		Long key = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConn();
			conn.setAutoCommit(false);
			pstmt = isGenerateKey ? conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS) : conn
					.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				pstmt.setObject(i + 1, objs[i]);
			}
			int i = pstmt.executeUpdate();
			if (i > 0) {
				rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					key = rs.getLong(1);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DBConnection.closeConn(rs, pstmt, false);
		}
		return key;
	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param isGenerateKey
	 * @return
	 */
	public static boolean updateWithoutCommit(final String sql,
			final Object[] objs) {
        boolean flag = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				pstmt.setObject(i + 1, objs[i]);
			}
			int i = pstmt.executeUpdate();
			if (i > 0) {
				flag = true;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DBConnection.closeConn(rs, pstmt, false);
		}
		return flag;
	}

	/**
	 * 
	 * @param sql
	 * @param obj
	 * @param isGenerateKey
	 * @return
	 */
	public static boolean updateWithCommit(final String sql,
			final Object[] objs) {
        boolean flag = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				pstmt.setObject(i + 1, objs[i]);
			}
			int i = pstmt.executeUpdate();
			if (i > 0) {
				flag = true;
				conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException ex) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
		} finally {
			DBConnection.closeConn(rs, pstmt, true);
		}
		return flag;
	}
}
