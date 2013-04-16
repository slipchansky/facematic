package com.slipchansky.fm.factory.builders;

import org.dom4j.Element;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TabSheet;

public class TabSheetBuilder extends ComponentContainerBuilder {
	
	@Override
	public Class getBuildingClass() {
		return TabSheet.class;
	}

	@Override
	protected void addComponent (AbstractComponentContainer container,
			AbstractComponent component, Element node) {
		    TabSheet tabSheet = (TabSheet)container;
		    tabSheet.addTab(component);
	}
	
	
}
