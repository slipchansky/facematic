package com.slipchansky.vabuilder.tests;

import com.slipchansky.fm.mvc.annotations.FmViewComponent;
import com.slipchansky.fm.mvc.annotations.FmController;
import com.vaadin.ui.Component;

public class ControllerB {
		@FmController(name="parent")
		Controller parentCotroller;
		
		@FmViewComponent(name="view")
		Component view;
		
		@FmViewComponent(name="nestedB")
		Component firstNestedB;

		public Controller getParentCotroller() {
			return parentCotroller;
		}

		public Component getView() {
			return view;
		}

		public Component getFirstNestedB() {
			return firstNestedB;
		}
		
}
