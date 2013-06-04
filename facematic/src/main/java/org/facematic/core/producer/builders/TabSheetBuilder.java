package org.facematic.core.producer.builders;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.utils.FacematicUtils;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;

/**
 * @author "Stanislav Lipchansky"
 * 
 */
public class TabSheetBuilder extends ComponentContainerBuilder {
	private final static Logger logger = LoggerFactory
			.getLogger(TabSheetBuilder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.facematic.core.producer.builders.ComponentContainerBuilder#
	 * getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return TabSheet.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.producer.builders.ComponentContainerBuilder#build(
	 * org.facematic.core.producer.FaceProducer, java.lang.Object,
	 * org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object oComponent,
			Element configuration) {
		super.build(builder, oComponent, configuration);
		TabSheet tab = (TabSheet) oComponent;
		String showTabs = configuration.attributeValue("hideTabs");
		if ("true".equals(showTabs)) {
			tab.hideTabs(true);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.producer.builders.ComponentContainerBuilder#addComponent
	 * (com.vaadin.ui.AbstractComponentContainer,
	 * com.vaadin.ui.AbstractComponent, org.dom4j.Element)
	 */
	@Override
	protected void addComponent(AbstractComponentContainer container,
			AbstractComponent component, Element node) {

		if (component instanceof Window) {
			return;
		}

		final TabSheet tabSheet = (TabSheet) container;
		tabSheet.addTab(component);
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				FacematicUtils.updateTabFocus(tabSheet.getSelectedTab());
			}
		});

	}

}
