package org.facematic.core.producer.builders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.nvo.Item;
import org.facematic.core.producer.FaceProducer;
import org.facematic.utils.BeanConverter;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;

/**
 * @author "Stanislav Lipchansky"
 * 
 */
public class SelectBuilder extends AbstractFieldBuilder {
	private final static Logger logger = LoggerFactory.getLogger(SelectBuilder.class);

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

		AbstractSelect select = (AbstractSelect) oComponent;
		
		String beanClass, beanIdField, beanCaptionField;
		beanClass = configuration.attributeValue("beanClass");
		beanIdField = configuration.attributeValue("beanIdField");
		beanCaptionField = configuration.attributeValue("beanCaptionField");
		
		if (beanClass != null) {
			addConverter (select, beanClass, beanIdField, beanCaptionField);
		}
		
		if (beanCaptionField != null) {
			select.setItemCaptionPropertyId(beanCaptionField);
		}
		
		
		String enumName = configuration.attributeValue("enum");
		if (!addEnumDataSource(select, enumName)) {
			List<Item> items = NvoItemBuilder.buildItems(builder, configuration);
			if (items != null && items.size() > 0) {
				addContainerDataSource(select, items);
			}
		}
		super.build(builder, oComponent, configuration);
	}

	private boolean addEnumDataSource(AbstractSelect select, String enumName) {
		if (enumName == null) {
			return false;
		}

		String original = enumName;
		String a[] = enumName.split("/");
		String enumField = null;
		if (a.length == 2) {
			enumField = a[1];
			enumName = a[0];
		}
		try {
			Class enumClass = Class.forName(enumName);
			BeanItemContainer indexedContainer = new BeanItemContainer(enumClass, Arrays.asList(enumClass.getEnumConstants()));
			select.setContainerDataSource(indexedContainer);
			if (enumField != null)
				select.setItemCaptionPropertyId(enumField);
			if (select instanceof ComboBox)((ComboBox) select).setTextInputAllowed(false);
			return true;
		} catch (ClassNotFoundException e) {
			logger.warn("Enum " + enumName + " not found");
			return false;
		} catch (Exception ee) {
			logger.warn("Can't implement enum datasource: " + original);
			return false;
		}
	}

	protected void addContainerDataSource(AbstractSelect select,
			List<Item> options) {
		final Container container = new IndexedContainer();
		for (Item item : options) {
			container.addItem(item.toString());
		}
		select.setContainerDataSource(container);
	}
	
	private void addConverter(AbstractSelect field, String beanClass, String beanIdField, String beanCaptionField) {
		Class clazz;
		try {
			clazz = Class.forName(beanClass);
		} catch (ClassNotFoundException e) {
			return;
		}
		if (beanIdField==null || "".equals(beanIdField.trim())) {
			beanIdField = "id";
		}
		Converter converter = new BeanConverter (clazz, beanIdField, beanCaptionField);
		field.setConverter (converter);
	}
	

}
