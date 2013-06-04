package org.facematic.site.showcase.databinding;

import org.facematic.site.showcase.annotations.ShowFiles;

@ShowFiles(java = true, xml = false)
public class ComboReference {
	
	int id;
	String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
