package com.ots.service.sys.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ots.dao.jdbc.BaseDao4JDBC;
import com.ots.dao.utils.CountObjectMapper;
import com.ots.dao.utils.DBConnection;
import com.ots.dao.utils.ObjectMapper;
import com.ots.dao.utils.StringMapper;
import com.ots.domain.oil.Client;
import com.ots.domain.sys.Tresource;
import com.ots.dto.base.PageFilter;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.JsonClient;
import com.ots.dto.sys.User;
import com.ots.service.sys.ClientServiceI;
import com.ots.utils.MD5Util;

@Service
public class ClientServiceImpl implements ClientServiceI {

	private static final Map<String, String> orderMap = new HashMap<String, String>();

	public ClientServiceImpl() {
		orderMap.put("loginname", "login_name");
		orderMap.put("createDate", "create_date");
		orderMap.put("cellPhone", "cell_phone");
		orderMap.put("name", "name");
		orderMap.put("phone", "phone");
		orderMap.put("email", "email");
		orderMap.put("status", "status");
	}

	@Override
	public List<Client> dataGrid(Client client, PageFilter ph) {
		final String roleSql = roleSql(client.getRoleNames());
		final String sql = "select * from client t, address a, client_role cr where t.address_id = a.id and t.id = cr.client_id "
				// + roleSql + orderSql(ph) + " limit ?, ?";
				+ roleSql
				+ orderSql(ph)
				+ " offset ? rows fetch next ? rows only";
		Object[] objs = null;
		if (roleSql.equals("")) {
			objs = new Object[] { (ph.getPage() - 1) * ph.getRows(),
					ph.getRows() };
		} else {
			objs = new Object[] { client.getLoginUserId(),
					(ph.getPage() - 1) * ph.getRows(), ph.getRows() };
		}
		@SuppressWarnings("unchecked")
		final List<Client> clients = (List<Client>) BaseDao4JDBC.query(
				sql, objs, new ClientObjectMapper());
		return clients;
	}

	private String roleSql(final String roleNames) {
		String result = "";
		if (roleNames.contains("manager")) {
			result = "";
		} else if (roleNames.contains("trader")) {
			result = " and t.trader_id = ? ";
		} else if (roleNames.contains("client")) {
			result = " and t.id = ? ";
		}
		return result;
	}

	private String orderSql(final PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + orderMap.get(ph.getSort()) + " "
					+ ph.getOrder();
		}
		return orderString;
	}

	@Override
	public Long count(Client client, PageFilter ph) {
		final String roleSql = roleSql(client.getRoleNames());
		final String sql = "select count(*) from client t, address a, client_role cr where t.address_id = a.id and t.id = cr.client_id"
				+ roleSql;
		Object[] objs = null;
		if (roleSql.equals("")) {
			objs = new Object[] {};
		} else {
			objs = new Object[] { client.getLoginUserId() };
		}
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	@Override
	public void add(Client client) {
		String sql = "insert into address(city,state,zipcode) values(?,?,?)";
		Object[] objs = { client.getCity(), client.getState(),
				client.getZipcode() };

		Long addressId = BaseDao4JDBC.insertReturnGeneratedKey(sql,
				objs, true);
		if (addressId == null) {
			DBConnection.closeConn();
		} else {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			sql = "insert into client(create_date,name,phone,cell_phone,email,login_name,password,status,address_id,trader_id) values(?,?,?,?,?,?,?,?,?,?)";
			objs = new Object[] { format.format(System.currentTimeMillis()),
					client.getName(), client.getPhone(), client.getCellPhone(),
					client.getEmail(), client.getLoginname(),
					MD5Util.md5(client.getPassword()), 0, addressId,
					client.getTraderId() };
			Long clientId = BaseDao4JDBC.insertReturnGeneratedKey(sql,
					objs, true);

			if (clientId == null) {
				DBConnection.closeConn();
			} else {
				sql = "update address set client_id = ? where id = ? ";
				objs = new Object[] { clientId, addressId };
				boolean flag = BaseDao4JDBC.updateWithoutCommit(sql,
						objs);
				if (!flag) {
					DBConnection.closeConn();
				} else {
					String[] roleIds = client.getRoleIds().split(",");
					String[] sqls = new String[roleIds.length];
					for (int i = 0; i < roleIds.length; i++) {
						sql = "insert into client_role(client_id, role_id) values("
								+ clientId + ",'" + roleIds[i] + "')";
						sqls[i] = sql;
						BaseDao4JDBC.batchUpdate(sqls, true);
					}
				}
			}
		}

	}

	@Override
	public void delete(Long id) {

		String sql = "delete from address where client_id=?";
		final Object[] objs = { id };
		boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
		if (!flag) {
			DBConnection.closeConn();
		} else {
			sql = "delete from client_role where client_id=?";
			flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
			if (!flag) {
				DBConnection.closeConn();
			} else {
				sql = "delete from client where id=?";
				BaseDao4JDBC.updateWithCommit(sql, objs);
			}
		}
	}

	@Override
	public void edit(Client client) {
		String sql = "update client set login_name=?, name=?, email=?, phone=?, cell_phone=?, trader_id =? "
				+ "where id=?";
		Object[] objs = { client.getLoginname(), client.getName(),
				client.getEmail(), client.getPhone(), client.getCellPhone(),
				client.getTraderId(), client.getId() };
		boolean flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
		if (!flag) {
			DBConnection.closeConn();
		} else {
			if (StringUtils.isNotBlank(client.getPassword())) {
				sql = "update client set password=? where id=?";
				objs = new Object[] { MD5Util.md5(client.getPassword()),
						client.getId() };
				flag = BaseDao4JDBC.updateWithoutCommit(sql, objs);
				if (!flag) {
					DBConnection.closeConn();
				} else {
					sql = "update address set city=?, zipcode=?,state=? where id=?";
					objs = new Object[] { client.getCity(),
							client.getZipcode(), client.getState(),
							client.getAddressId() };
					BaseDao4JDBC.updateWithCommit(sql, objs);
				}
			} else {
				sql = "update address set city=?, zipcode=?,state=? where id=?";
				objs = new Object[] { client.getCity(), client.getZipcode(),
						client.getState(), client.getAddressId() };
				BaseDao4JDBC.updateWithCommit(sql, objs);
			}
		}
	}

	@Override
	public Client get(Long id) {
		final String sql = "select * from client t, address a, client_role cr where t.address_id = a.id and cr.client_id = t.id and t.id = ?";
		final Object[] objs = { id };
		return (Client) BaseDao4JDBC.find(sql, objs,
				new ClientObjectMapper());
	}

	@Override
	public JsonClient getClientSimpleInfo(Long id) {
		String sql = "select t.id, t.client_level, t.stock from client t where t.id = ?";
		Object[] objs = { id };
		JsonClient client = (JsonClient) BaseDao4JDBC.find(sql, objs,
				new SimpleClientMapper());
		// if ("silver".equals(client.getLevel())) {
		// sql = "select sum(volume) as month_amount from oil_transaction "
		// +
		// "where status != 2 and date_format(date,'%Y-%m')= date_format(now(),'%Y-%m') and client_id = ? ";
		sql = "select sum(volume) as month_amount from oil_transaction "
				+ "where status != 2 and left(create_date,7)= ? and client_id = ? ";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objs = new Object[] {
				format.format(System.currentTimeMillis()).substring(0, 7), id };
		Long monthAmount = (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
		client.setMonthAmount(monthAmount);
		// }
		return client;
	}

	@Override
	public User login(User user) {
		final String sql = "select t.*, sr.name as roleName from client t, client_role cr, sys_role sr where t.id = cr.client_id and cr.role_id = sr.id and t.login_name = ? and t.password = ?";
		final Object[] objs = { user.getLoginname(),
				MD5Util.md5(user.getPassword()) };
		Client client = (Client) BaseDao4JDBC.find(sql, objs,
				new ClientLoginObjectMapper());
		User userLogin = null;
		if (client != null) {
			userLogin = new User();
			userLogin.setLoginname(client.getLoginname());
			userLogin.setPassword(client.getPassword());
			userLogin.setId(client.getId());
			userLogin.setName(client.getName());
			userLogin.setRoleNames(client.getRoleNames());
		}
		return userLogin;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> resourceList(Long id) {
		final String sql = "select r.url from client c, client_role cr, sys_role_resource rr, sys_resource r where c.id = cr.client_id and cr.role_id = rr.role_id and r.id = rr.resource_id and c.id = ?";
		final Object[] objs = { id };
		return (List<String>) BaseDao4JDBC.query(sql, objs,
				new StringMapper());

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tresource> resourceTree(final int type, final Long clientId) {
		final String sql = "select r.* from client c, client_role cr, sys_role_resource rr, sys_resource r where c.id = cr.client_id and cr.role_id = rr.role_id and r.id = rr.resource_id and c.id = ? and r.resourcetype = ? order by r.seq";
		final Object[] objs = { clientId, type };
		return (List<Tresource>) BaseDao4JDBC.query(sql, objs,
				new ResourceObjectMapper());

	}

	@Override
	public boolean editUserPwd(SessionInfo sessionInfo, String oldPwd,
			String pwd) {
		boolean flag = false;
		Client client = get(sessionInfo.getId());
		if (client.getPassword().equalsIgnoreCase(MD5Util.md5(oldPwd))) {
			String sql = "update client set password=? where id=?";
			final Object[] objs = { MD5Util.md5(pwd), client.getId() };
			flag = BaseDao4JDBC.update(sql, objs, false);
		}
		return flag;
	}

	@Override
	public Long getByLoginName(Client client) {
		final String sql = "select count(*) from client t where t.login_name = ?";
		final Object[] objs = { client.getLoginname() };
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	@Override
	public Long getByLoginNameAndId(Client client) {
		final String sql = "select count(*) from client t where t.id != ? and t.login_name = ?";
		final Object[] objs = { client.getId(), client.getLoginname() };
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	@Override
	public Long getClientsByTraderId(Long traderId) {
		final String sql = "select count(*) from client t where t.trader_id = ?";
		final Object[] objs = { traderId };
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	@Override
	public Long getTransactionsByClientId(Long clientId) {
		final String sql = "select count(*) from oil_transaction where client_id = ?";
		final Object[] objs = { clientId };
		return (Long) BaseDao4JDBC.find(sql, objs,
				new CountObjectMapper());
	}

	class ClientObjectMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final Client client = new Client();
			try {
				client.setId(rs.getLong("id"));
				client.setName(rs.getString("name"));
				client.setCellPhone(rs.getString("cell_phone"));
				client.setPhone(rs.getString("phone"));
				client.setEmail(rs.getString("email"));
				client.setCreateDate(rs.getString("create_date"));
				client.setLoginname(rs.getString("login_name"));
				client.setStatus(rs.getInt("status"));
				client.setCity(rs.getString("city"));
				client.setZipcode(rs.getString("zipcode"));
				client.setState(rs.getString("state"));
				client.setRoleIds(rs.getString("role_id"));
				client.setTraderId(rs.getLong("trader_id"));
				client.setAddressId(rs.getLong("address_id"));
				client.setPassword(rs.getString("password"));

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return client;
		}
	}

	class TreeObjectMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final Client client = new Client();
			try {
				client.setId(rs.getLong("id"));
				client.setLoginname(rs.getString("login_name"));
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return client;
		}
	}

	class SimpleClientMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final JsonClient client = new JsonClient();
			try {
				client.setId(rs.getLong("id"));
				client.setStack(rs.getDouble("stock"));
				if (rs.getInt("client_level") == 0) {
					client.setLevel("silver");
				} else if (rs.getInt("client_level") == 1) {
					client.setLevel("gold");
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return client;
		}
	}

	class ClientLoginObjectMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final Client client = new Client();
			try {
				client.setId(rs.getLong("id"));
				client.setName(rs.getString("name"));
				client.setPassword(rs.getString("password"));
				client.setLoginname(rs.getString("login_name"));
				client.setRoleNames(rs.getString("roleName"));

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return client;
		}
	}

	class ResourceObjectMapper implements ObjectMapper {
		public Object mapping(ResultSet rs) {
			final Tresource resource = new Tresource();
			try {
				resource.setId(rs.getLong("id"));
				resource.setName(rs.getString("name"));
				resource.setIcon(rs.getString("icon"));
				resource.setUrl(rs.getString("url"));
				Tresource pr = new Tresource();
				pr.setId(rs.getLong("pid"));
				resource.setResource(pr);

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return resource;
		}
	}

	@Override
	public List<Tree> tree(SessionInfo sessionInfo) {
		List<Tree> lt = new ArrayList<Tree>();
		String roleSql = roleSql(sessionInfo.getRoleNames());
		final String sql = "select t.id, t.login_name from client t where 1 = 1 "
				+ roleSql;
		Object[] objs = null;
		if (roleSql.equals("")) {
			objs = new Object[] {};
		} else {
			objs = new Object[] { sessionInfo.getId() };
		}
		@SuppressWarnings("unchecked")
		final List<Client> clients = (List<Client>) BaseDao4JDBC.query(
				sql, objs, new TreeObjectMapper());
		if ((clients != null) && (clients.size() > 0)) {
			for (Client client : clients) {
				Tree tree = new Tree();
				tree.setId(client.getId().toString());
				tree.setText(client.getLoginname());
				lt.add(tree);
			}
		}
		return lt;
	}
}
