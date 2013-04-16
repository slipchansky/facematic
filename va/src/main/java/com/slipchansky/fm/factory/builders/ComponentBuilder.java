package com.slipchansky.fm.factory.builders;


import com.slipchansky.fm.factory.ComponentFactory;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;

import org.dom4j.Attribute;
import org.dom4j.Element;

public class ComponentBuilder extends BeanBuilder {
	
	public Class getBuildingClass () {
		return AbstractComponent.class;
	}
	
	public void build (ComponentFactory builder, Object oComponent, Element configuration) {
		super.build (builder, oComponent, configuration);
		
		Attribute sizeFullAttr = configuration.attribute("sizeFull");
		if (sizeFullAttr != null) {
			if ("true".equals(sizeFullAttr.getValue())) {
				((AbstractComponent)oComponent).setSizeFull();
			} else {
				((AbstractComponent)oComponent).setSizeUndefined();
			}
		}
		
	}

}
