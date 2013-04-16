package com.slipchansky.fm.factory.builders;

import java.util.List;

import org.dom4j.Element;

import com.slipchansky.fm.factory.ComponentFactory;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Panel;

public class PanelBuilder extends ComponentBuilder {

	@Override
	public Class getBuildingClass() {
		return Panel.class;
	}

	@Override
	public void build(ComponentFactory builder, Object oComponent, Element configuration) {

		super.build(builder, oComponent, configuration);

		Object inner = prepareInnerComponent(builder, configuration);
		if (inner != null)
		if (inner instanceof AbstractComponent) {
			Panel panel = (Panel) oComponent;
			panel.setContent((AbstractComponent) inner);
		}
		
	}

	protected Object prepareInnerComponent(ComponentFactory builder, Element configuration) {
		
		Object inner = null;
		List<Element> elements = configuration.elements();

		if (elements.size() == 0)
			return null;
		
		Element firstNested = elements.get(0);
		
		try {
			inner = builder.build(firstNested);
			if (inner != null) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inner;
	}
}
