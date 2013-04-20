package com.slipchansky.vabuilder.tests;

import com.slipchansky.fm.mvc.FmBaseController;
import com.slipchansky.fm.mvc.annotations.FmViewComponent;
import com.slipchansky.fm.mvc.annotations.FmController;
import com.vaadin.ui.Component;

public class Controller implements FmBaseController {
	
	@FmController(name="first")
	ControllerA controllerA;
	
	@FmController(name="second")
	ControllerB controllerB;
	
	@FmViewComponent(name="view")
	Component view;
	
	@FmViewComponent(name="first")
	Component compositeFirst;
	
	@FmViewComponent(name="second")
	Component compositeSecond;
	
	@FmViewComponent(name="first.nestedA")
	Component firstNestedA;
	
	@FmViewComponent(name="second.nestedB")
	Component firstNestedB;
	
	@FmViewComponent(name="third.nestedB")
	Component thirdNestedB;

	public ControllerA getControllerA() {
		return controllerA;
	}

	public ControllerB getControllerB() {
		return controllerB;
	}

	public Component getView() {
		return view;
	}

	public Component getCompositeFirst() {
		return compositeFirst;
	}

	public Component getCompositeSecond() {
		return compositeSecond;
	}

	public Component getFirstNestedA() {
		return firstNestedA;
	}

	public Component getFirstNestedB() {
		return firstNestedB;
	}

	public Component getThirdNestedB() {
		return thirdNestedB;
	}

	@Override
	public void main() {
		// TODO Auto-generated method stub
		int k = 0;
		k++;
		
	}
	
	
	
}
