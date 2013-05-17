package org.facematic.core.producer.builders;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.nvo.Item;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ListSelect;

/**
 * @author "Stanislav Lipchansky"
 * 
 */
public class SelectBuilder extends AbstractFieldBuilder {
	private final static Logger logger = LoggerFactory
			.getLogger(SelectBuilder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.producer.builders.AbstractFieldBuilder#getBuildingClass
	 * ()
	 */
	@Override
	public Class getBuildingClass() {
		return AbstractSelect.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.producer.builders.AbstractFieldBuilder#build(org.facematic
	 * .core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, final Object oComponent,
			Element configuration) {
		super.build(builder, oComponent, configuration);

		AbstractSelect select = (AbstractSelect) oComponent;
		List<Item> items = NvoItemBuilder.buildItems(builder, configuration);
		if (items != null && items.size() > 0) {
			addContainerDataSource(select, items);
			
			
		}
	}

	protected void addContainerDataSource(AbstractSelect select, List<Item> options) {
		
		
		final Container container = new IndexedContainer();
		for (Item item : options) {
			container.addItem(item.toString());
		}
		select.setContainerDataSource(container);
	}

}
