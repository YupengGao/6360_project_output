package com.ots.dto.oil;

public class StatisticsInfo {

	private String loginName;
	private Double volume;
	private Double commission;
	private Double commissionOil;
	private Double price;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Double getCommissionOil() {
		return commissionOil;
	}

	public void setCommissionOil(Double commissionOil) {
		this.commissionOil = commissionOil;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
