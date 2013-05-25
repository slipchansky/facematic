package org.facematic.util.data.managedcontainer;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public enum Action {
	EDIT("Изменить"), 
	UP("Переместить назад"), 
	DOWN("Переместить вперед"), 
	DELETE ("Удалить");
	
	private String description;
	
	private Action (String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
