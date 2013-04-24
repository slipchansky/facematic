package org.facematic.core.producer.tests;

import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.annotations.FmController;
import com.vaadin.ui.Component;

public class ControllerA {
	
	@FmController(name="parent")
	Controller parentCotroller;
	
	@FmViewComponent(name="view")
	Component view;
	
	@FmViewComponent(name="nestedA")
	Component firstNestedA;

	public Controller getParentCotroller() {
		return parentCotroller;
	}

	public Component getView() {
		return view;
	}

	public Component getFirstNestedA() {
		return firstNestedA;
	}
	
	

}
