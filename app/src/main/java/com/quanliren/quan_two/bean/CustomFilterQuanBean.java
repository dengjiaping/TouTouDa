package com.quanliren.quan_two.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName="CustomFilterBean")
public class CustomFilterQuanBean implements Serializable{
	@DatabaseField
	public String title;
	@DatabaseField
	public String defaultValue;
	@DatabaseField(id=true)
	public String key;
	@DatabaseField
	public int id;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public CustomFilterQuanBean(String title, String defaultValue, String key,
			int id) {
		super();
		this.title = title;
		this.defaultValue = defaultValue;
		this.key = key;
		this.id = id;
	}
	public CustomFilterQuanBean() {
		super();
		// TODO Auto-generated constructor stub
	}
}