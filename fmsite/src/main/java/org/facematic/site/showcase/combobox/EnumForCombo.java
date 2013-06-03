package org.facematic.site.showcase.combobox;

public enum EnumForCombo {
	ONE("First"), TWO("Second"), THREE("Third");
	
	private String description;
	
	private EnumForCombo (String description) {
		this.description = description;
	};
	
	public String getDescription() {
		return description;
	};
	
	
	
	

}
