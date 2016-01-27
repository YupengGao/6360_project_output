package com.ots.service.oil.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ots.dao.jdbc.BaseDao4JDBC;
import com.ots.dao.utils.CountObjectMapper;
import com.ots.dao.utils.DBConnection;
import com.ots.dao.utils.ObjectMapper;
import com.ots.domain.oil.OilSell;
import com.ots.dto.base.PageFilter;
import com.ots.dto.oil.StatisticsInfo;
import com.ots.dto.oil.TransactionSearch;
import com.ots.service.oil.OilSellServiceI;

@Service
public class OilSellServiceImpl implements OilSellServiceI {
	@Override
	public List<OilSell> dataGrid(final TransactionSearch search,
			final PageFilter ph) {

		String sql = null;
		Object[] objs = { search.getLoginUserId(),
				(ph.getPage() - 1) * ph.getRows(), ph.getRows() };
		if ("trader".equals(search.getRoleName())) {
			sql = "select t.*,c.*,u.loginname from oil_transaction t, client c, sys_user u where t.type = 1 and t.client_id = c.id and c.trader_id = u.id and c.trader_id = ? ";
		} else if ("client".equals(search.getRoleName())) {
			sql = "select t.*,c.*,u.loginname from oil_transaction t, client c, sys_user u where t.type = 1 and t.client_id = c.id and c.trader_id = u.id and t.client_id = ?";
		} else {
			sql = "select t.*,c.*,u.loginname from oil_transaction t, client c, sys_user u where t.type = 1 and t.client_id = c.id and c.trader_id = u.id ";
			objs = new Object[] { (ph.getPage() - 1) * ph.getRows(),
					ph.getRows() };
		}
		sql += whereSql(search) + orderSql(ph)
				+ " offset ? rows fetch next ? rows only ";

		@SuppressWarnings("unchecked")
		final List<OilSell> oilBuies = (List<OilSell>) BaseDao4JDBC
				.query(sql, objs, new DataGridMapper());
		return oilBuies;
	}

	private String whereSql(final TransactionSearch search) {
		String whereString = "";
		boolean isStartDateNotBlank = StringUtils.isNotBlank(search
				.getStartDate());
		boolean isEndDateNotBlank = StringUtils.isNotBlank(search.getEndDate());
		if (isStartDateNotBlank) {
			if (isEndDateNotBlank) {
				// whereString = whereString + " and date(t.date) >= '"
				// + search.getStartDate() + "' and date(t.date) <= '"
				// + search.getEndDate() + "'";
				whereString = whereString
						+ " and convert(datetime, t.create_date) >= '"
						+ search.getStartDate()
						+ "' and convert(datetime,t.create_date) <= '"
						+ search.getEndDate() + "'";
			} else {
				whereString = whereString
						+ " and convert(datetime,t.create_date) >= '"
						+ search.getStartDate() + "'";
			}
		} else {
			if (isEndDateNotBlank) {
				whereString = whereString
						+ " and convert(datetime,t.create_date) <= '"
						+ search.getEndDate() + "'";
			}
		}
		return whereString;
	}

	private String orderSql(final PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public Long count(final TransactionSearch search, final PageFilter ph) {
		String sql = null;
		Object[] objs = { search.getLoginUserId() };
		if ("trader".equals(search.getRoleName())) {
			sql = "select count(*) from oil_transaction t, client c where t.type = 1 and t.client_id = c.id and c.trader_id = ? ";
		} else if ("client".equals(search.getRoleName())) {
			sql = "select count(*) from oil_transaction t, client c where t.type = 1 and t.client_id = c.id and t.client_id = ?";
		} else {
			sql = "select count(*) from oil_transaction t, client c where t.type = 1 and t.client_id = c.id ";
			objs = new Object[] {};
		}
		sql += whereSql(search);
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	@Override
	public void add(final OilSell oilSell) {
		String sql = null;
		Object[] objs = null;
		Double volume = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (oilSell.getPayMethod() == 0) {
			sql = "insert into oil_transaction(create_date,client_id,volume,type, commission,pay_method,price,cause_upgrade) values(?,?,?,?,?,?,?,?)";
			objs = new Object[] { format.format(System.currentTimeMillis()),
					oilSell.getClientId(), oilSell.getVolume(),
					oilSell.getType(), oilSell.getCommission(), 0,
					oilSell.getVolume() * 2, oilSell.getCauseUpgrade() };
			volume = oilSell.getVolume().doubleValue();
		} else {
			sql = "insert into oil_transaction(create_date,client_id,volume,type, commission_oil,pay_method,price,cause_upgrade) values(?,?,?,?,?,?,?,?)";
			objs = new Object[] { format.format(System.currentTimeMillis()),
					oilSell.getClientId(), oilSell.getVolume(),
					oilSell.getType(), oilSell.getCommissionOil(), 1,
					oilSell.getVolume() * 2, oilSell.getCauseUpgrade() };
			volume = oilSell.getVolume().doubleValue()
					+ oilSell.getCommissionOil();
		}
		boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
		if (!flag) {
			DBConnection.closeConn();
		} else {
			sql = "update client set stock = stock - ? where id = ?";
			if ("gold".equals(oilSell.getLevel())) {
				sql = "update client set client_level = 1, stock = stock - ? where id = ?";
			}
			objs = new Object[] { volume, oilSell.getClientId() };
			BaseDao4JDBC.updateWithCommit(sql, objs);
		}
	}

	@Override
	public void delete(final Long id) {
		String sql = null;
		Double volume = null;
		Object[] objs = null;
		OilSell oldOilSell = this.get(id);
		boolean isLastRecord = isLastRecord(oldOilSell);
		if (isLastRecord) {
			if (oldOilSell.getCauseUpgrade() == 1) {
				sql = "update client set client_level = 0, stock=stock+? where id = ?";
			} else {
				sql = "update client set stock=stock+? where id = ? ";
			}
			if (oldOilSell.getPayMethod() == 0) {
				volume = oldOilSell.getVolume().doubleValue();
			} else {
				volume = oldOilSell.getVolume().doubleValue()
						+ oldOilSell.getCommissionOil();
			}
			objs = new Object[] { volume, oldOilSell.getClientId() };
			boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
			if (!flag) {
				DBConnection.closeConn();
			} else {
				sql = "delete from oil_transaction where id=?";
				objs = new Object[] { id };
				BaseDao4JDBC.updateWithCommit(sql, objs);
			}
		}
	}

	private boolean isLastRecord(OilSell oilSell) {
		final String sql = "select count(*) from oil_transaction where status != 2 and convert(datetime,create_date) > (select convert(datetime,create_date) from oil_transaction t where t.id=?) and client_id =?";
		final Object[] objs = { oilSell.getId(), oilSell.getClientId() };
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper()) == 0;
	}

	@Override
	public boolean isLastRecord(Long id) {
		OilSell oldOilSell = this.get(id);
		return isLastRecord(oldOilSell);
	}

	@Override
	public void edit(final OilSell oilSell) {
		String sql = null;
		Object[] objs = null;
		Double volume = null;
		OilSell oldOilSell = this.get(oilSell.getId());
		if (oilSell.getPayMethod() == 0) {
			sql = "update oil_transaction set volume = ?, commission = ?, commission_oil = ?, price = ?, pay_method = ? ";
			objs = new Object[] { oilSell.getVolume(), oilSell.getCommission(),
					0, oilSell.getVolume() * 2, 0, oilSell.getId() };
			volume = oilSell.getVolume().doubleValue()
					- oldOilSell.getVolume().doubleValue()
					- oldOilSell.getCommissionOil();
		} else {
			sql = "update oil_transaction set volume = ?, commission = ?, commission_oil = ?, price = ?, pay_method = ? ";
			objs = new Object[] { oilSell.getVolume(), 0,
					oilSell.getCommissionOil(), oilSell.getVolume() * 2, 1,
					oilSell.getId() };
			volume = oilSell.getVolume().doubleValue()
					- oldOilSell.getVolume().doubleValue()
					+ (oilSell.getCommissionOil() - oldOilSell
							.getCommissionOil());
		}
		if ("silver".equals(oilSell.getLevel())
				&& oilSell.getCauseUpgrade() == 1) {
			sql = sql + ",cause_upgrade = 0 ";
		} else if ("gold".equals(oilSell.getLevel())
				&& oilSell.getCauseUpgrade() == 0) {
			sql = sql + ",cause_upgrade = 1 ";
		}
		sql = sql + " where id = ? ";
		boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
		if (!flag) {
			DBConnection.closeConn();
		} else {
			if ("gold".equals(oilSell.getLevel())) {
				sql = "update client set client_level = 1, stock = stock - ? where id = ?";
			} else {
				sql = "update client set client_level = 0, stock = stock - ? where id = ?";
			}
			objs = new Object[] { volume, oilSell.getClientId() };
			BaseDao4JDBC.updateWithCommit(sql, objs);
		}
	}

	@Override
	public OilSell get(final Long id) {
		String sql = "select * from oil_transaction t, client c where t.client_id = c.id and t.id = ?";
		Object[] objs = { id };
		OilSell oilSell = (OilSell) BaseDao4JDBC.find(sql, objs,
				new OilSellObjectMapper());
		// sql =
		// "select sum(volume) as month_amount from oil_transaction where date_format(date,'%Y-%m')= date_format(now(),'%Y-%m') and client_id = ? ";
		sql = "select sum(volume) as month_amount from oil_transaction "
				+ "where status != 2 and left(create_date,7)= ? and client_id = ? ";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objs = new Object[] {
				format.format(System.currentTimeMillis()).substring(0, 7),
				oilSell.getClientId() };
		Long monthAmount = (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
		oilSell.setMonthAmount(monthAmount);
		return oilSell;
	}

	class DataGridMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final OilSell oilSell = new OilSell();
			try {
				oilSell.setId(rs.getLong("id"));
				oilSell.setVolume(rs.getLong("volume"));
				oilSell.setClientName(rs.getString("login_name"));
				oilSell.setClientId(rs.getLong("client_id"));
				oilSell.setDate(rs.getTimestamp("create_date"));
				oilSell.setCommission(rs.getDouble("commission"));
				oilSell.setCommissionOil(rs.getDouble("commission_oil"));
				oilSell.setFee(rs.getDouble("price"));
				oilSell.setStatus(rs.getInt("status"));
				oilSell.setPayDate(rs.getTimestamp("pay_date"));
				oilSell.setCancelDate(rs.getTimestamp("cancel_date"));
				oilSell.setTraderName(rs.getString("loginname"));

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return oilSell;
		}
	}

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

	class OilSellObjectMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final OilSell oilSell = new OilSell();
			try {
				oilSell.setId(rs.getLong("id"));
				oilSell.setVolume(rs.getLong("volume"));
				oilSell.setClientId(rs.getLong("client_id"));
				oilSell.setClientName(rs.getString("login_name"));
				oilSell.setDate(rs.getDate("create_date"));
				oilSell.setLevel(rs.getInt("client_level") == 0 ? "silver"
						: "gold");
				oilSell.setCommission(rs.getDouble("commission"));
				oilSell.setCommissionOil(rs.getDouble("commission_oil"));
				oilSell.setStack(rs.getDouble("stock"));
				oilSell.setPayMethod(rs.getInt("pay_method"));
				oilSell.setCauseUpgrade(rs.getInt("cause_upgrade"));

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return oilSell;
		}
	}

	@Override
	public void pay(Long id) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String sql = "update oil_transaction set status = 1, pay_date=? where id = ?";
		BaseDao4JDBC.update(sql,
				new Object[] { format.format(System.currentTimeMillis()), id },
				false);
	}

	@Override
	public void cancel(Long id) {
		String sql = null;
		Double volume = null;
		Object[] objs = null;
		OilSell oldOilSell = this.get(id);
		boolean isLastRecord = isLastRecord(oldOilSell);
		if (isLastRecord) {
			if (oldOilSell.getCauseUpgrade() == 1) {
				sql = "update client set client_level = 0, stock=stock+? where id = ?";
			} else {
				sql = "update client set stock=stock+? where id = ? ";
			}
			if (oldOilSell.getPayMethod() == 0) {
				volume = oldOilSell.getVolume().doubleValue();
			} else {
				volume = oldOilSell.getVolume().doubleValue()
						+ oldOilSell.getCommissionOil();
			}
			objs = new Object[] { volume, oldOilSell.getClientId() };
			boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
			if (!flag) {
				DBConnection.closeConn();
			} else {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				sql = "update oil_transaction set status = 2, cancel_date=? where id = ?";
				objs = new Object[] {
						format.format(System.currentTimeMillis()), id };
				BaseDao4JDBC.updateWithCommit(sql, objs);
			}
		}
	}

	@Override
	public List<StatisticsInfo> dataGrid4Statistics(TransactionSearch search,
			PageFilter ph) {
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
	public Long count4Statistics(TransactionSearch search, PageFilter ph) {
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
