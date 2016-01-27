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

/**
 * The client management controller.
 * 
 * @author Martin
 */
@Controller
@RequestMapping("/client")
public class ClientController extends BaseController {

	@Autowired
	private UserServiceI userService;

	@Autowired
	private ClientServiceI clientService;

	/**
	 * Show client main page.
	 * @param request
	 * @return the path of client main page.
	 */
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/client";
	}

	/**
	 * Search client information.
	 * @param request
	 * @param client
	 * @param ph
	 * @return Grid for DataGrid
	 */
	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(HttpServletRequest request, Client client,
			PageFilter ph) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		client.setRoleNames(sessionInfo.getRoleNames());
		client.setLoginUserId(sessionInfo.getId());
		Grid grid = new Grid();
		grid.setRows(clientService.dataGrid(client, ph));
		grid.setTotal(clientService.count(client, ph));
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
		try {
			userService.editUserPwd(sessionInfo, oldPwd, pwd);
			j.setSuccess(true);
			j.setMsg("password was successfully modified！");
		} catch (Exception e) {
			j.setMsg("old password is not correct!");
		}
		return j;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		if (!sessionInfo.getLoginname().equals("admin")) {
			request.setAttribute("loginUserId", sessionInfo.getId().toString());
		}

		return "/admin/clientAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(Client client) {
		Json j = new Json();
		Long count = clientService.getByLoginName(client);
		User user = null;
		if (count == 0) {
			user = new User();
			user.setLoginname(client.getLoginname());
			user = userService.getByLoginName(user);
		}
		if (user != null || count > 0) {
			j.setMsg("The user with the same login name already exists!");
		} else {
			try {
				clientService.add(client);
				j.setSuccess(true);
				j.setMsg("successfully add！");
			} catch (Exception e) {
				j.setMsg(e.getMessage());
			}

		}
		return j;
	}

	@RequestMapping("/get")
	@ResponseBody
	public Client get(Long id) {
		return clientService.get(id);
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		final Long count = clientService.getTransactionsByClientId(id);
		if (count > 0) {
			j.setMsg("The client owns several transactions, so cannot be deleted!");
		} else {
			try {
				clientService.delete(id);
				j.setMsg("Delete successfully!");
				j.setSuccess(true);
			} catch (Exception e) {
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		Client client = clientService.get(id);
		request.setAttribute("client", client);
		return "/admin/clientEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(Client client) {
		Json j = new Json();
		Long count = clientService.getByLoginNameAndId(client);
		User user = null;
		if (count == 0) {
			user = new User();
			user.setLoginname(client.getLoginname());
			user = userService.getByLoginName(user);
		}
		if (user != null || count > 0) {
			j.setMsg("The user with the same login name already exists!");
		} else {
			try {
				clientService.edit(client);
				j.setSuccess(true);
				j.setMsg("Edit successfully!");
			} catch (ServiceException e) {
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

	@RequestMapping("/tree")
	@ResponseBody
	public List<Tree> tree(HttpServletRequest request) {
		final SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		return clientService.tree(sessionInfo);
	}
}
