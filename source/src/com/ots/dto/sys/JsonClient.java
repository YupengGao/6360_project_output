package com.ots.dto.sys;

public class JsonClient implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Double stack;
	private String level;
	private Long monthAmount;

	public Double getStack() {
		return stack;
	}

	public void setStack(Double stack) {
		this.stack = stack;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Long getMonthAmount() {
		return monthAmount;
	}

	public void setMonthAmount(Long monthAmount) {
		this.monthAmount = monthAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}