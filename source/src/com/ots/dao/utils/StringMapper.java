package com.ots.dao.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringMapper implements ObjectMapper {
	public Object mapping(final ResultSet rs) {
		String result = null;
		try {
			result = rs.getString(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
