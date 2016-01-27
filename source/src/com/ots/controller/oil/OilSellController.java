package com.ots.controller.oil;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ots.controller.base.BaseController;
import com.ots.domain.oil.OilSell;
import com.ots.dto.base.Grid;
import com.ots.dto.base.Json;
import com.ots.dto.base.PageFilter;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.oil.TransactionSearch;
import com.ots.dto.sys.JsonClient;
import com.ots.framework.constant.GlobalConstant;
import com.ots.service.oil.OilSellServiceI;
import com.ots.service.sys.ClientServiceI;

@Controller
@RequestMapping("/sell")
public class OilSellController extends BaseController {

	@Autowired
	private OilSellServiceI oilSellService;

	@Autowired
	private ClientServiceI clientService;

	@RequestMapping("/manager")
	public String manager() {
		return "/oil/oilSell";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(HttpServletRequest request, TransactionSearch search,
			PageFilter ph) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		search.setRoleName(sessionInfo.getRoleNames());
		search.setLoginUserId(sessionInfo.getId());
		Grid grid = new Grid();
		grid.setRows(oilSellService.dataGrid(search, ph));
		grid.setTotal(oilSellService.count(search, ph));
		return grid;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);
		if (sessionInfo.getRoleNames().equals("client")) {
			JsonClient client = clientService.getClientSimpleInfo(sessionInfo
					.getId());
			request.setAttribute("client", client);
			request.getSession().setAttribute("oldLevel", client.getLevel());

		}
		return "/oil/sellAdd";
	}

	@RequestMapping("/loadClient")
	@ResponseBody
	public JSONObject loadClient(HttpServletRequest request) {
		final String clientId = request.getParameter("clientId").substring(1);
		final JsonClient simpleClient = clientService.getClientSimpleInfo(Long
				.valueOf(clientId));
		request.getSession().setAttribute("oldLevel", simpleClient.getLevel());
		return JSONObject.fromObject(simpleClient);
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(HttpServletRequest request, OilSell oilSell) {

		Json j = new Json();
		if ("silver".equals(request.getSession().getAttribute("oldLevel"))
				&& "gold".equals(oilSell.getLevel())) {
			oilSell.setCauseUpgrade(1);
		} else {
			oilSell.setCauseUpgrade(0);
		}
			try {
				oilSell.setType(1);
				oilSellService.add(oilSell);
				j.setSuccess(true);
				j.setMsg("Add successfully!");
			} catch (Exception e) {
				j.setMsg(e.getMessage());
			}
		return j;
	}

	@RequestMapping("/pay")
	@ResponseBody
	public Json pay(Long id) {
		Json j = new Json();
		
			oilSellService.pay(id);
			j.setMsg("Successfully done!");
			j.setSuccess(true);

		return j;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		boolean isLastRecord = oilSellService.isLastRecord(id);
		if (isLastRecord) {
			oilSellService.delete(id);
			j.setMsg("Successfully done!");
			j.setSuccess(true);
		}
		else {
			j.setMsg("This transaction is not your latest record, so it cannot be deleted!");
		}
		return j;
	}

	
	@RequestMapping("/canEdit")
	@ResponseBody
	public Json canEdit(HttpServletRequest request, Long id) {
		Json j = new Json();
		boolean isLastRecord = oilSellService.isLastRecord(id);
		if (isLastRecord) {
			j.setSuccess(true);
		}
		else {
			j.setMsg("This transaction is not your latest record, so it cannot be deleted!");
		}
		return j;
	}


	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		OilSell oilSell = oilSellService.get(id);
		request.setAttribute("oilSell", oilSell);
		return "/oil/sellEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(OilSell oilSell) {
		Json j = new Json();
		try {
			oilSellService.edit(oilSell);
			j.setSuccess(true);
			j.setMsg("Edit successfully!");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/cancel")
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		boolean isLastRecord = oilSellService.isLastRecord(id);
		if (isLastRecord) {
			oilSellService.cancel(id);
			j.setMsg("Successfully done!");
			j.setSuccess(true);
		} else {
			j.setMsg("This transaction is not your latest record, so it cannot be canceled!");
		}
		return j;
	}
}
