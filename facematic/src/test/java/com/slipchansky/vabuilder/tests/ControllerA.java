package com.slipchansky.vabuilder.tests;

import com.slipchansky.fm.mvc.annotations.FmViewComponent;
import com.slipchansky.fm.mvc.annotations.FmController;
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
