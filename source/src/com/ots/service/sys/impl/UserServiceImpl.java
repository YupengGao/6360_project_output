package com.ots.service.sys.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ots.dao.BaseDaoI;
import com.ots.domain.sys.Tresource;
import com.ots.domain.sys.Trole;
import com.ots.domain.sys.Tuser;
import com.ots.dto.base.PageFilter;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.User;
import com.ots.framework.constant.GlobalConstant;
import com.ots.service.sys.UserServiceI;
import com.ots.utils.MD5Util;

@Service
public class UserServiceImpl implements UserServiceI {

	@Autowired
	private BaseDaoI<Tuser> userDao;

	@Autowired
	private BaseDaoI<Trole> roleDao;


	@Override
	public void add(User u) {
		Tuser t = new Tuser();
		BeanUtils.copyProperties(u, t);

		List<Trole> roles = new ArrayList<Trole>();
		if (u.getRoleIds() != null) {
			for (String roleId : u.getRoleIds().split(",")) {
				roles.add(roleDao.get(Trole.class, Long.valueOf(roleId)));
			}
		}
		t.setRoles(new HashSet<Trole>(roles));

		t.setPassword(MD5Util.md5(u.getPassword()));
		t.setState(GlobalConstant.ENABLE);
		SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		t.setCreatedatetime(format.format(System.currentTimeMillis()));
		userDao.save(t);
	}

	@Override
	public void delete(Long id) {
		Tuser t = userDao.get(Tuser.class, id);
		del(t);
	}

	private void del(Tuser t) {
		userDao.delete(t);
	}

	@Override
	public void edit(User user) {
		Tuser t = userDao.get(Tuser.class, user.getId());
		t.setLoginname(user.getLoginname());
		t.setName(user.getName());
//		List<Trole> roles = new ArrayList<Trole>();
//		if (user.getRoleIds() != null) {
//			for (String roleId : user.getRoleIds().split(",")) {
//				roles.add(roleDao.get(Trole.class, Long.valueOf(roleId)));
//			}
//		}
//		t.setRoles(new HashSet<Trole>(roles));
		t.setSex(user.getSex());
		if (user.getPassword() != null && !"".equals(user.getPassword())) {
			t.setPassword(MD5Util.md5(user.getPassword()));
		}
		userDao.update(t);
	}

	@Override
	public User get(Long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Tuser t = userDao.get(
				"from Tuser t  left join fetch t.roles role where t.id = :id",
				params);
		User u = new User();
		BeanUtils.copyProperties(t, u);

		if (t.getRoles() != null && !t.getRoles().isEmpty()) {
			String roleIds = "";
			String roleNames = "";
			boolean b = false;
			for (Trole role : t.getRoles()) {
				if (b) {
					roleIds += ",";
					roleNames += ",";
				} else {
					b = true;
				}
				roleIds += role.getId();
				roleNames += role.getName();
			}
			u.setRoleIds(roleIds);
			u.setRoleNames(roleNames);
		}
		return u;
	}

	@Override
	public User login(User user) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginname", user.getLoginname());
		params.put("password", MD5Util.md5(user.getPassword()));
		Tuser t = userDao
				.get("from Tuser t where t.loginname = :loginname and t.password = :password",
						params);
		if (t != null) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			String roleNames = "";
			Set<Trole> roles = t.getRoles();
			Iterator<Trole> iter = roles.iterator();
			int i = 0;
			while (iter.hasNext()) {
				if (i == 0) {
					roleNames += iter.next().getName();
				} else {
					roleNames += "," + iter.next().getName();
				}
			}
			u.setRoleNames(roleNames);
			return u;
		}
		return null;
	}

	@Override
	public List<String> resourceList(Long id) {
		List<String> resourceList = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Tuser t = userDao
				.get("from Tuser t join fetch t.roles role join fetch role.resources resource where t.id = :id",
						params);
		if (t != null) {
			Set<Trole> roles = t.getRoles();
			if ((roles != null) && !roles.isEmpty()) {
				for (Trole role : roles) {
					Set<Tresource> resources = role.getResources();
					if ((resources != null) && !resources.isEmpty()) {
						for (Tresource resource : resources) {
							if ((resource != null)
									&& (resource.getUrl() != null)) {
								resourceList.add(resource.getUrl());
							}
						}
					}
				}
			}
		}
		return resourceList;
	}

	@Override
	public List<User> dataGrid(User user, PageFilter ph) {
		List<User> ul = new ArrayList<User>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tuser t ";
		List<Tuser> l = userDao.find(
				hql + whereHql(user, params)
						+ roleSql(user.getRoleNames(), user.getLoginname())
						+ orderHql(ph), params, ph.getPage(), ph.getRows());
		for (Tuser t : l) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			Set<Trole> roles = t.getRoles();
			if ((roles != null) && !roles.isEmpty()) {
				String roleIds = "";
				String roleNames = "";
				boolean b = false;
				for (Trole tr : roles) {
					if (b) {
						roleIds += ",";
						roleNames += ",";
					} else {
						b = true;
					}
					roleIds += tr.getId();
					roleNames += tr.getName();
				}
				u.setRoleIds(roleIds);
				u.setRoleNames(roleNames);
			}
			ul.add(u);
		}
		return ul;
	}

	private String roleSql(final String roleNames, final String loginName) {
		String result = "";
		if (roleNames.contains("manager")) {
			result = "";
		} else if (roleNames.contains("trader")) {
			result = " and t.loginname = '" + loginName + "'";
		}
		return result;
	}

	@Override
	public Long count(User user, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tuser t ";
		return userDao.count("select count(*) " + hql + whereHql(user, params),
				params);
	}

	private String whereHql(User user, Map<String, Object> params) {
		String hql = "";
		if (user != null) {
			hql += " where 1=1 ";
			if (user.getName() != null) {
				hql += " and t.name like :name";
				params.put("name", "%%" + user.getName() + "%%");
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public boolean editUserPwd(SessionInfo sessionInfo, String oldPwd,
			String pwd) {
		Tuser u = userDao.get(Tuser.class, sessionInfo.getId());
		if (u.getPassword().equalsIgnoreCase(MD5Util.md5(oldPwd))) {// 说明原密码输入正确
			u.setPassword(MD5Util.md5(pwd));
			return true;
		}
		return false;
	}

	@Override
	public User getByLoginName(User user) {
		Tuser t = userDao.get("from Tuser t  where t.loginname = '"
				+ user.getLoginname() + "'");
		User u = new User();
		if (t != null) {
			BeanUtils.copyProperties(t, u);
		} else {
			return null;
		}
		return u;
	}

	@Override
	public User getByLoginNameAndId(User user) {
		Tuser t = userDao.get("from Tuser t where t.id != " + user.getId() + " and t.loginname = '"
				+ user.getLoginname() + "'");
		User u = new User();
		if (t != null) {
			BeanUtils.copyProperties(t, u);
		} else {
			return null;
		}
		return u;
	}
	
	@Override
	public List<Tree> tree() {
		List<Tuser> l = null;
		List<Tree> lt = new ArrayList<Tree>();

		l = userDao
				.find("select distinct t from Tuser t where t.loginname <> 'admin'");

		if ((l != null) && (l.size() > 0)) {
			for (Tuser r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				tree.setText(r.getLoginname());
				lt.add(tree);
			}
		}
		return lt;
	}
}
