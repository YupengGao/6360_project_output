package com.ots.controller.oil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ots.controller.base.BaseController;
import com.ots.dto.base.Grid;
import com.ots.dto.base.PageFilter;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.oil.TransactionSearch;
import com.ots.framework.constant.GlobalConstant;
import com.ots.service.oil.StatisticsServiceI;

@Controller
@RequestMapping("/sum")
public class StatisticsController extends BaseController {

	@Autowired
	private StatisticsServiceI statisticsService;

	@RequestMapping("/statistics")
	public String manager(HttpServletRequest request) {
		return "/oil/statistics";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(HttpServletRequest request, TransactionSearch search,
			PageFilter ph) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);

		search.setRoleName(sessionInfo.getRoleNames());
		search.setLoginUserId(sessionInfo.getId());
		if (StringUtils.isNotBlank(search.getStartDate())) {
			search.setStartDate(dateConvert(search.getStartDate(), "yyyy-MM-dd"));
			search.setStatisticsKind("day");
		} else if (StringUtils.isNotBlank(search.getEndDate())) {
			// search.setEndDate(dateConvert(search.getEndDate(), "yyyy-MM"));
			search.setStatisticsKind("month");
		} else {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String day = format.format(System.currentTimeMillis()).substring(0,
					10);
			search.setStatisticsKind("day");
			search.setStartDate(day);
		}

		Grid grid = new Grid();
		grid.setRows(statisticsService.dataGrid(search, ph));
		grid.setTotal(statisticsService.count(search, ph));
		return grid;
	}

	private String dateConvert(String oldString, String format) {
		String newString = null;
		SimpleDateFormat formatDate = new SimpleDateFormat(format);
		SimpleDateFormat formatString = new SimpleDateFormat("MM/dd/yyyy");
		try {
			newString = formatDate.format(formatString.parse(oldString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newString;
	}
}
