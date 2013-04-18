package com.slipchansky.fm.factory.builders;

import org.dom4j.Element;

import com.slipchansky.fm.factory.FaceFactory;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TabSheet;

public class TabSheetBuilder extends ComponentContainerBuilder {
	
	@Override
	public Class getBuildingClass() {
		return TabSheet.class;
	}
	
	

	@Override
	public void build(FaceFactory builder, Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		TabSheet tab = (TabSheet)oComponent;
		String showTabs = configuration.attributeValue("hideTabs");
		if ("true".equals(showTabs)) {
			tab.hideTabs(true);
		}
		
	}



	@Override
	protected void addComponent (AbstractComponentContainer container,
			AbstractComponent component, Element node) {
		    TabSheet tabSheet = (TabSheet)container;
		    tabSheet.addTab(component);
	}
	
	
}
