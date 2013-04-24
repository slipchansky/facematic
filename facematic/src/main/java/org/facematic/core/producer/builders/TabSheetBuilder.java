package org.facematic.core.producer.builders;

import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TabSheet;

public class TabSheetBuilder extends ComponentContainerBuilder {
	
	@Override
	public Class getBuildingClass() {
		return TabSheet.class;
	}
	
	

	@Override
	public void build(FaceProducer builder, Object oComponent, Element configuration) {
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
