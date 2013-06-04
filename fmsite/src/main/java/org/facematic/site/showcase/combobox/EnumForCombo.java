package org.facematic.site.showcase.combobox;

import org.facematic.site.showcase.annotations.ShowFiles;

@ShowFiles(java = true, xml = false)
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
