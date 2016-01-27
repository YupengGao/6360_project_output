package com.ots.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ots.controller.base.BaseController;
import com.ots.domain.oil.Client;
import com.ots.dto.base.Grid;
import com.ots.dto.base.Json;
import com.ots.dto.base.PageFilter;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.User;
import com.ots.framework.constant.GlobalConstant;
import com.ots.service.base.ServiceException;
import com.ots.service.sys.ClientServiceI;
import com.ots.service.sys.UserServiceI;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserServiceI userService;

	@Autowired
	private ClientServiceI clientService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/user";
	}

	@RequestMapping("/tree")
	@ResponseBody
	public List<Tree> tree() {
		return userService.tree();
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(HttpServletRequest request, User user, PageFilter ph) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		user.setRoleNames(sessionInfo.getRoleNames());
		user.setLoginname(sessionInfo.getLoginname());
		Grid grid = new Grid();
		grid.setRows(userService.dataGrid(user, ph));
		grid.setTotal(userService.count(user, ph));
		return grid;
	}

	@RequestMapping("/editPwdPage")
	public String editPwdPage(HttpServletRequest request) {
		return "/admin/userEditPwd";
	}

	@RequestMapping("/editUserPwd")
	@ResponseBody
	public Json editUserPwd(HttpServletRequest request, String oldPwd,
			String pwd) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		Json j = new Json();

		boolean flag = false;
		if (sessionInfo.isClient()) {
			flag = clientService.editUserPwd(sessionInfo, oldPwd, pwd);
		} else {
			flag = userService.editUserPwd(sessionInfo, oldPwd, pwd);
		}
		if (flag) {
			j.setSuccess(true);
			j.setMsg("password was successfully modified!");
		} else {
			j.setMsg("old password is not correct!");
		}

		return j;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {

		request.setAttribute("sexList", GlobalConstant.sexlist);
		return "/admin/userAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(User user) {
		Json j = new Json();
		User u = userService.getByLoginName(user);
		Long count = null;
		if (u == null) {
			final Client client = new Client();
			client.setLoginname(user.getLoginname());
			count = clientService.getByLoginName(client);
		}
		if (u != null || count > 0) {
			j.setMsg("The user with the same login name already exists!");
		} else {
			try {
				userService.add(user);
				j.setSuccess(true);
				j.setMsg("successfully done！");
			} catch (Exception e) {
				j.setMsg(e.getMessage());
			}

		}
		return j;
	}

	@RequestMapping("/get")
	@ResponseBody
	public User get(Long id) {
		return userService.get(id);
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		final Long count = clientService.getClientsByTraderId(id);
		if (count > 0) {
			j.setMsg("The trader owns several clients, so cannot be deleted!");
		} else {
			try {
				userService.delete(id);
				j.setMsg("successfully delete！");
				j.setSuccess(true);
			} catch (Exception e) {
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		User u = userService.get(id);
		request.setAttribute("user", u);
		request.setAttribute("sexList", GlobalConstant.sexlist);
		return "/admin/userEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(User user) {
		Json j = new Json();
		User u = userService.getByLoginNameAndId(user);
		Long count = null;
		if (u == null) {
			final Client client = new Client();
			client.setLoginname(user.getLoginname());
			count = clientService.getByLoginName(client);
		}
		if (u != null || count > 0) {
			j.setMsg("The user with the same login name already exists!");
		} else {
			try {
				userService.edit(user);
				j.setSuccess(true);
				j.setMsg("Eidt successfully!");
			} catch (ServiceException e) {
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

}
