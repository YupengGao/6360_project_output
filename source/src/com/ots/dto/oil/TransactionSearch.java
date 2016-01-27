package com.ots.dto.oil;

public class TransactionSearch {

	private String startDate;
	private String endDate;
	private Long loginUserId;
	private String roleName;
	private String statisticsKind = "day";

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getLoginUserId() {
		return loginUserId;
	}

	public void setLoginUserId(Long loginUserId) {
		this.loginUserId = loginUserId;
	}

	public String getStatisticsKind() {
		return statisticsKind;
	}

	public void setStatisticsKind(String statisticsKind) {
		this.statisticsKind = statisticsKind;
	}
}
