package com.slipchansky.fm.factory.builders;

import org.dom4j.Element;

import com.slipchansky.fm.factory.ComponentFactory;
import com.vaadin.ui.AbstractSelect;

public class SelectBuilder extends ComponentBuilder {

	@Override
	public Class getBuildingClass() {
		return AbstractSelect.class;
	}

	@Override
	public void build(ComponentFactory builder, Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
	}
	
	

}