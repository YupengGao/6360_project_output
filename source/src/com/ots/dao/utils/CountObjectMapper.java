package com.ots.dao.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountObjectMapper implements ObjectMapper {
	public Object mapping(final ResultSet rs) {
		Long count = null;
		try {
			count = rs.getLong(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return count;
	}
}
