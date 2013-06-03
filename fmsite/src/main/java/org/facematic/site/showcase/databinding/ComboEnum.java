package org.facematic.site.showcase.databinding;

public enum ComboEnum {
	ONE("First"), TWO("Second"), THREE("Third");
	
	private String description;
	
	private ComboEnum (String description) {
		this.description = description;
	};
	
	public String getDescription() {
		return description;
	};
}
