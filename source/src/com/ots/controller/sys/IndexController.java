package com.ots.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ots.controller.base.BaseController;
import com.ots.dto.base.Json;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.User;
import com.ots.framework.constant.GlobalConstant;
import com.ots.service.sys.ClientServiceI;
import com.ots.service.sys.ResourceServiceI;
import com.ots.service.sys.RoleServiceI;
import com.ots.service.sys.UserServiceI;

@Controller
@RequestMapping("/admin")
public class IndexController extends BaseController {

	@Autowired
	private UserServiceI userService;

	@Autowired
	private ClientServiceI clientService;

	@Autowired
	private ResourceServiceI resourceService;
	
	@Autowired
	private RoleServiceI roleService;

	@RequestMapping("/index")
	public String index(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		if ((sessionInfo != null) && (sessionInfo.getId() != null)) {
			return "/index";
		}
		return "/login";
	}

	@RequestMapping("/index1")
	public String index1(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		if ((sessionInfo != null) && (sessionInfo.getId() != null)) {
			return "/index1";
		} else {
			String loginName = request.getParameter("loginname");
			String password = request.getParameter("password");
			User user = new User();
			user.setLoginname(loginName);
			user.setPassword(password);
			HttpSession session = request.getSession();
			if (login2(user, session))
				return "/index1";
			else
				return "/login";
		}

	}

	@RequestMapping("/index2")
	public String index2(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		if ((sessionInfo != null) && (sessionInfo.getId() != null)) {
			return "/index2";
		}
		return "/login";
	}

	@ResponseBody
	@RequestMapping("/login")
	public Json login(User user, HttpSession session) {
		Json j = new Json();
		boolean isClient = false;
		User sysuser = userService.login(user);
		if (sysuser == null) {
			sysuser = clientService.login(user);
			isClient = true;
		}
		if (sysuser != null) {
			j.setSuccess(true);
			j.setMsg("登陆成功！");
			SessionInfo sessionInfo = new SessionInfo();
			sessionInfo.setId(sysuser.getId());
			sessionInfo.setLoginname(user.getLoginname());
			sessionInfo.setPassword(user.getPassword());
			sessionInfo.setName(sysuser.getName());
			sessionInfo.setRoleNames(sysuser.getRoleNames());
			sessionInfo.setClient(isClient);
			// user.setIp(IpUtil.getIpAddr(getRequest()));
			if (isClient) {
				sessionInfo.setResourceList(clientService.resourceList(sysuser
						.getId()));
			} else {
				sessionInfo.setResourceList(userService.resourceList(sysuser
						.getId()));
			}
			sessionInfo.setResourceAllList(resourceService
					.resourceAllList());
			List<Tree> roles =  roleService.tree();
			for (Tree tree : roles) {
				if (tree.getText().equals("client")) {
					session.setAttribute("clientRoleId", tree.getId());
				} else if (tree.getText().equals("trader")) {
					session.setAttribute("traderRoleId", tree.getId());
				}
			}
			session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
		} else {
			j.setMsg("login name or password is wrong!");
		}
		return j;
	}

	@RequestMapping("/login2")
	public boolean login2(User user, HttpSession session) {
		User sysuser = userService.login(user);
		if (sysuser != null) {
			SessionInfo sessionInfo = new SessionInfo();
			sessionInfo.setId(sysuser.getId());
			sessionInfo.setLoginname(sysuser.getLoginname());
			sessionInfo.setName(sysuser.getName());
			// user.setIp(IpUtil.getIpAddr(getRequest()));
			sessionInfo.setResourceList(userService.resourceList(sysuser
					.getId()));
			sessionInfo.setResourceAllList(resourceService.resourceAllList());
			session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
			return true;
		} else {
			return false;
		}
	}

	@ResponseBody
	@RequestMapping("/logout")
	public Json logout(HttpSession session) {
		Json j = new Json();
		if (session != null) {
			session.invalidate();
		}
		j.setSuccess(true);
		j.setMsg("注销成功！");
		return j;
	}

}
