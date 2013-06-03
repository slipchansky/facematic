package org.facematic.site.showcase.databinding;

import java.util.Date;

public class DataBindingBean {
	String string;
	int  comboInt;
	ComboEnum comboEnum;
	Date date;
	
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public int getComboInt() {
		return comboInt;
	}
	public void setComboInt(int comboInt) {
		this.comboInt = comboInt;
	}
	public ComboEnum getComboEnum() {
		return comboEnum;
	}
	public void setComboEnum(ComboEnum comboEnum) {
		this.comboEnum = comboEnum;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "DataBindingBean [\n  string=" + string + ",\n  comboInt=" + comboInt
				+ ",\n  comboEnum=" + comboEnum + ",\n  date=" + date + "\n]";
	}
	
	

}
