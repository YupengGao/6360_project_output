package com.ots.service.oil.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ots.dao.jdbc.BaseDao4JDBC;
import com.ots.dao.utils.CountObjectMapper;
import com.ots.dao.utils.ObjectMapper;
import com.ots.dto.base.PageFilter;
import com.ots.dto.oil.StatisticsInfo;
import com.ots.dto.oil.TransactionSearch;
import com.ots.service.oil.StatisticsServiceI;

@Service
public class StatisticsServiceImpl implements StatisticsServiceI {

	class StatisticsMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final StatisticsInfo staInfo = new StatisticsInfo();
			try {
				staInfo.setLoginName(rs.getString("login_name"));
				staInfo.setVolume(rs.getDouble("volume"));
				staInfo.setCommission(rs.getDouble("commission"));
				staInfo.setCommissionOil(rs.getDouble("commission_oil"));
				staInfo.setPrice(rs.getDouble("price"));
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return staInfo;
		}
	}

	@Override
	public List<StatisticsInfo> dataGrid(TransactionSearch search, PageFilter ph) {
		String sql = null;
		Object[] objs = null;
		String statisticsKind = search.getStatisticsKind();
		String day = search.getStartDate();
		String month = search.getEndDate();
		if ("day".equals(statisticsKind)) {
			objs = new Object[] { search.getLoginUserId(), day,
					(ph.getPage() - 1) * ph.getRows(), ph.getRows() };
		} else if ("month".equals(statisticsKind)) {
			objs = new Object[] { search.getLoginUserId(), month,
					(ph.getPage() - 1) * ph.getRows(), ph.getRows() };
		}
		String dateSql = dateSql(statisticsKind);
		if ("trader".equals(search.getRoleName())) {
			sql = "select c.login_name, sum(volume) as volume, sum(commission) as commission, sum(t.commission_oil) as commission_oil, sum(price) as price from oil_transaction t, client c, sys_user u "
					+ "where t.client_id = c.id and c.trader_id = u.id and u.id = ? and t.status !=2 "
					+ dateSql + " group by c.login_name order by c.login_name";
		} else if ("client".equals(search.getRoleName())) {
			sql = "select c.login_name, sum(volume) as volume, sum(commission) as commission, sum(t.commission_oil) as commission_oil, sum(price) as price from oil_transaction t, client c "
					+ "where t.client_id = c.id and c.id = ? and t.status !=2 "
					+ dateSql + " group by c.login_name order by c.login_name";
		} else {
			sql = "select c.login_name,sum(volume) as volume,sum(commission) as commission,sum(t.commission_oil) as commission_oil,sum(price) as price from oil_transaction t, client c "
					+ "where t.client_id = c.id and t.status !=2 "
					+ dateSql
					+ " group by c.login_name order by c.login_name ";
			if ("day".equals(statisticsKind)) {
				objs = new Object[] { day, (ph.getPage() - 1) * ph.getRows(),
						ph.getRows() };
			} else if ("month".equals(statisticsKind)) {
				objs = new Object[] { month, (ph.getPage() - 1) * ph.getRows(),
						ph.getRows() };
			}
		}
		sql += " offset ? rows fetch next ? rows only ";

		@SuppressWarnings("unchecked")
		final List<StatisticsInfo> staList = (List<StatisticsInfo>) BaseDao4JDBC
				.query(sql, objs, new StatisticsMapper());
		return staList;
	}

	private String dateSql(String dateType) {
		String dateSql = "";
		if ("day".equals(dateType)) {
			dateSql = " and left(t.create_date,10) = ? ";
		} else if ("month".equals(dateType)) {
			dateSql = " and left(t.create_date,7) = ? ";
		}
		return dateSql;
	}

	@Override
	public Long count(TransactionSearch search, PageFilter ph) {
		String sql = null;
		Object[] objs = null;
		String statisticsKind = search.getStatisticsKind();
		String day = search.getStartDate();
		String month = search.getEndDate();
		if ("day".equals(statisticsKind)) {
			objs = new Object[] { search.getLoginUserId(), day };
		} else if ("month".equals(statisticsKind)) {
			objs = new Object[] { search.getLoginUserId(), month };
		}
		String dateSql = dateSql(statisticsKind);
		if ("trader".equals(search.getRoleName())) {
			sql = "select count(*) from oil_transaction t, client c, sys_user u "
					+ "where t.client_id = c.id and c.trader_id = u.id and u.id = ? and t.status !=2 "
					+ dateSql + " group by c.login_name order by c.login_name";
		} else if ("client".equals(search.getRoleName())) {
			sql = "select count(*) from oil_transaction t, client c "
					+ "where t.client_id = c.id and c.id = ? and t.status !=2 "
					+ dateSql + " group by c.login_name order by c.login_name";
		} else {
			sql = "select count(*) from oil_transaction t, client c "
					+ "where t.client_id = c.id and t.status !=2 " + dateSql
					+ " group by c.login_name order by c.login_name ";
			if ("day".equals(statisticsKind)) {
				objs = new Object[] { day };
			} else if ("month".equals(statisticsKind)) {
				objs = new Object[] { month };
			}
		}
		Long count = (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
		return count == null ? 0 : count;
	}
}
