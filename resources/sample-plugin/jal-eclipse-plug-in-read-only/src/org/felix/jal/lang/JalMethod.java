package org.felix.jal.lang;

public class JalMethod extends JalElement {
	private String params="";
	
	public JalMethod(String name, String typedName) {
		super(name, typedName);
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	

}
