package org.facematic.site.sandbox;

public class Snippet {
	String value;

	public Snippet() {
		super();
	}


	public Snippet(String value) {
		super();
		this.value = value;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public String toString() {
		return value;
	}
	
	
	
	

}
