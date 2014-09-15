package com.quanliren.quan_two.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ExchangeRemindBean")
public class ExchangeRemindBean {

	@DatabaseField(id=true)
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@DatabaseField()
	private String eaid;
	@DatabaseField
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEaid() {
		return eaid;
	}

	public void setEaid(String eaid) {
		this.eaid = eaid;
	}


	public ExchangeRemindBean(String eaid, String userId) {
		super();
		this.eaid = eaid;
		this.userId = userId;
	}

	public ExchangeRemindBean() {
		super();
		// TODO Auto-generated constructor stub
	}
}
