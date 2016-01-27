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
import com.ots.domain.oil.OilBuy;
import com.ots.dto.base.PageFilter;
import com.ots.dto.oil.TransactionSearch;
import com.ots.service.oil.OilBuyServiceI;

@Service
public class OilBuyServiceImpl implements OilBuyServiceI {
	@Override
	public List<OilBuy> dataGrid(final TransactionSearch search,
			final PageFilter ph) {

		String sql = null;
		Object[] objs = { search.getLoginUserId(),
				(ph.getPage() - 1) * ph.getRows(), ph.getRows() };
		if ("trader".equals(search.getRoleName())) {
			sql = "select t.*,c.*,u.loginname from oil_transaction t, client c, sys_user u where t.type = 0 and t.client_id = c.id and c.trader_id = u.id and c.trader_id = ? ";
		} else if ("client".equals(search.getRoleName())) {
			sql = "select t.*,c.*,u.loginname from oil_transaction t, client c, sys_user u where t.type = 0 and t.client_id = c.id and c.trader_id = u.id and t.client_id = ?";
		} else {
			sql = "select t.*,c.*,u.loginname from oil_transaction t, client c, sys_user u where t.type = 0 and t.client_id = c.id and c.trader_id = u.id ";
			objs = new Object[] { (ph.getPage() - 1) * ph.getRows(),
					ph.getRows() };
		}
		sql += whereSql(search) + orderSql(ph)
				+ "  offset ? rows fetch next ? rows only ";

		@SuppressWarnings("unchecked")
		final List<OilBuy> oilBuies = (List<OilBuy>) BaseDao4JDBC.query(
				sql, objs, new DataGridMapper());
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
			sql = "select count(*) from oil_transaction t, client c where t.type = 0 and t.client_id = c.id and c.trader_id = ? ";
		} else if ("client".equals(search.getRoleName())) {
			sql = "select count(*) from oil_transaction t, client c where t.type = 0 and t.client_id = c.id and t.client_id = ?";
		} else {
			sql = "select count(*) from oil_transaction t, client c where t.type = 0 and t.client_id = c.id ";
			objs = new Object[] {};
		}
		sql += whereSql(search);
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	@Override
	public void add(final OilBuy oilBuy) {
		String sql = null;
		Object[] objs = null;
		Double volume = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (oilBuy.getPayMethod() == 0) {
			sql = "insert into oil_transaction(create_date,client_id,volume,type, commission,pay_method,price,cause_upgrade) values(?,?,?,?,?,?,?,?)";
			objs = new Object[] { format.format(System.currentTimeMillis()),
					oilBuy.getClientId(), oilBuy.getVolume(), oilBuy.getType(),
					oilBuy.getCommission(), 0, oilBuy.getVolume() * 2,
					oilBuy.getCauseUpgrade() };
			volume = oilBuy.getVolume().doubleValue();
		} else {
			sql = "insert into oil_transaction(create_date,client_id,volume,type, commission_oil,pay_method,price,cause_upgrade) values(?,?,?,?,?,?,?,?)";
			objs = new Object[] { format.format(System.currentTimeMillis()),
					oilBuy.getClientId(), oilBuy.getVolume(), oilBuy.getType(),
					oilBuy.getCommissionOil(), 1, oilBuy.getVolume() * 2,
					oilBuy.getCauseUpgrade() };
			volume = oilBuy.getVolume().doubleValue()
					- oilBuy.getCommissionOil();
		}
		boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
		if (!flag) {
			DBConnection.closeConn();
		} else {
			sql = "update client set stock = stock + ? where id = ?";
			if ("gold".equals(oilBuy.getLevel())) {
				sql = "update client set client_level = 1, stock = stock + ? where id = ?";
			}
			objs = new Object[] { volume, oilBuy.getClientId() };
			BaseDao4JDBC.updateWithCommit(sql, objs);
		}
	}

	@Override
	public void delete(final Long id) {
		String sql = null;
		Double volume = null;
		Object[] objs = null;
		OilBuy oldOilBuy = this.get(id);
		boolean isLastRecord = isLastRecord(oldOilBuy);
		if (isLastRecord) {
			if (oldOilBuy.getCauseUpgrade() == 1) {
				sql = "update client set client_level = 0, stock=stock-? where id = ?";
			} else {
				sql = "update client set stock=stock-? where id = ? ";
			}
			if (oldOilBuy.getPayMethod() == 0) {
				volume = oldOilBuy.getVolume().doubleValue();
			} else {
				volume = oldOilBuy.getVolume().doubleValue()
						- oldOilBuy.getCommissionOil();
			}
			objs = new Object[] { volume, oldOilBuy.getClientId() };
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

	private boolean isLastRecord(OilBuy oilBuy) {
		final String sql = "select count(*) from oil_transaction where status != 2 and convert(datetime,create_date) > (select convert(datetime,create_date) from oil_transaction t where t.id=?) and client_id =?";
		final Object[] objs = { oilBuy.getId(), oilBuy.getClientId() };
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper()) == 0;
	}

	@Override
	public boolean isLastRecord(Long id) {
		OilBuy oldOilBuy = this.get(id);
		return isLastRecord(oldOilBuy);
	}

	@Override
	public void edit(final OilBuy oilBuy) {
		String sql = null;
		Object[] objs = null;
		Double volume = null;
		OilBuy oldOilBuy = this.get(oilBuy.getId());
		if (oilBuy.getPayMethod() == 0) {
			sql = "update oil_transaction set volume = ?, commission = ?, commission_oil = ?, price = ?, pay_method = ? ";
			objs = new Object[] { oilBuy.getVolume(), oilBuy.getCommission(),
					0, oilBuy.getVolume() * 2, 0, oilBuy.getId() };
			volume = oilBuy.getVolume().doubleValue()
					- oldOilBuy.getVolume().doubleValue()
					+ oldOilBuy.getCommissionOil();
		} else {
			sql = "update oil_transaction set volume = ?, commission = ?, commission_oil = ?, price = ?, pay_method = ? ";
			objs = new Object[] { oilBuy.getVolume(), 0,
					oilBuy.getCommissionOil(), oilBuy.getVolume() * 2, 1,
					oilBuy.getId() };
			volume = oilBuy.getVolume().doubleValue()
					- oldOilBuy.getVolume().doubleValue()
					- (oilBuy.getCommissionOil() - oldOilBuy.getCommissionOil());
		}
		if ("silver".equals(oilBuy.getLevel()) && oilBuy.getCauseUpgrade() == 1) {
			sql = sql + ",cause_upgrade = 0 ";
		} else if ("gold".equals(oilBuy.getLevel())
				&& oilBuy.getCauseUpgrade() == 0) {
			sql = sql + ",cause_upgrade = 1 ";
		}
		sql = sql + " where id = ? ";
		boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
		if (!flag) {
			DBConnection.closeConn();
		} else {
			if ("gold".equals(oilBuy.getLevel())) {
				sql = "update client set client_level = 1, stock = stock + ? where id = ?";
			} else {
				sql = "update client set client_level = 0, stock = stock + ? where id = ?";
			}
			objs = new Object[] { volume, oilBuy.getClientId() };
			BaseDao4JDBC.updateWithCommit(sql, objs);
		}
	}

	@Override
	public OilBuy get(final Long id) {
		String sql = "select * from oil_transaction t, client c where t.client_id = c.id and t.id = ?";
		Object[] objs = { id };
		OilBuy oilBuy = (OilBuy) BaseDao4JDBC.find(sql, objs,
				new OilBuyObjectMapper());
		// sql =
		// "select sum(volume) as month_amount from oil_transaction where status != 2 date_format(date,'%Y-%m')= date_format(now(),'%Y-%m') and client_id = ? ";
		sql = "select sum(volume) as month_amount from oil_transaction "
				+ "where status != 2 and left(create_date,7)= ? and client_id = ? ";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objs = new Object[] {
				format.format(System.currentTimeMillis()).substring(0, 7),
				oilBuy.getClientId() };
		Long monthAmount = (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
		oilBuy.setMonthAmount(monthAmount);
		return oilBuy;
	}

	class DataGridMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final OilBuy oilBuy = new OilBuy();
			try {
				oilBuy.setId(rs.getLong("id"));
				oilBuy.setVolume(rs.getLong("volume"));
				oilBuy.setClientName(rs.getString("login_name"));
				oilBuy.setClientId(rs.getLong("client_id"));
				oilBuy.setDate(rs.getTimestamp("create_date"));
				oilBuy.setCommission(rs.getDouble("commission"));
				oilBuy.setCommissionOil(rs.getDouble("commission_oil"));
				oilBuy.setFee(rs.getDouble("price"));
				oilBuy.setStatus(rs.getInt("status"));
				oilBuy.setPayDate(rs.getTimestamp("pay_date"));
				oilBuy.setCancelDate(rs.getTimestamp("cancel_date"));
				oilBuy.setTraderName(rs.getString("loginname"));

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return oilBuy;
		}
	}

	class OilBuyObjectMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final OilBuy oilBuy = new OilBuy();
			try {
				oilBuy.setId(rs.getLong("id"));
				oilBuy.setVolume(rs.getLong("volume"));
				oilBuy.setClientId(rs.getLong("client_id"));
				oilBuy.setClientName(rs.getString("login_name"));
				oilBuy.setDate(rs.getDate("create_date"));
				oilBuy.setLevel(rs.getInt("client_level") == 0 ? "silver"
						: "gold");
				oilBuy.setCommission(rs.getDouble("commission"));
				oilBuy.setCommissionOil(rs.getDouble("commission_oil"));
				oilBuy.setStack(rs.getDouble("stock"));
				oilBuy.setPayMethod(rs.getInt("pay_method"));
				oilBuy.setCauseUpgrade(rs.getInt("cause_upgrade"));

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return oilBuy;
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
		OilBuy oldOilBuy = this.get(id);
		boolean isLastRecord = isLastRecord(oldOilBuy);
		if (isLastRecord) {
			if (oldOilBuy.getCauseUpgrade() == 1) {
				sql = "update client set client_level = 0, stock=stock-? where id = ?";
			} else {
				sql = "update client set stock=stock-? where id = ? ";
			}
			if (oldOilBuy.getPayMethod() == 0) {
				volume = oldOilBuy.getVolume().doubleValue();
			} else {
				volume = oldOilBuy.getVolume().doubleValue()
						- oldOilBuy.getCommissionOil();
			}
			objs = new Object[] { volume, oldOilBuy.getClientId() };
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
}
