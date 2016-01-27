package com.ots.dao.utils;

import java.sql.ResultSet;

public interface ObjectMapper {
	public Object mapping(final ResultSet rs); 
}
