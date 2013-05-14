package org.facematic.core.producer.builders;

import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TabSheet;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class TabSheetBuilder extends ComponentContainerBuilder {
	
	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentContainerBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return TabSheet.class;
	}
	
	

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentContainerBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		TabSheet tab = (TabSheet)oComponent;
		String showTabs = configuration.attributeValue("hideTabs");
		if ("true".equals(showTabs)) {
			tab.hideTabs(true);
		}
		
	}



	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentContainerBuilder#addComponent(com.vaadin.ui.AbstractComponentContainer, com.vaadin.ui.AbstractComponent, org.dom4j.Element)
	 */
	@Override
	protected void addComponent (AbstractComponentContainer container,
			AbstractComponent component, Element node) {
		    TabSheet tabSheet = (TabSheet)container;
		    tabSheet.addTab(component);
	}
	
}
