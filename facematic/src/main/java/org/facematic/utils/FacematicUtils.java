package org.facematic.utils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class FacematicUtils {
	
	public static void setTableDatasource (Table table, Container container ){
		Object[] visible = table.getVisibleColumns();
		table.setContainerDataSource(container);
		table.setVisibleColumns(visible);
	}
	
	public static void setComboBoxData (ComboBox combo, Class beanClass, Collection collection, String idFieldName, String captionFieldName){
		String idGetterName = "get"+idFieldName.substring(0, 1).toUpperCase()+idFieldName.substring(1);  
		String captionGetterName = "get"+captionFieldName.substring(0, 1).toUpperCase()+captionFieldName.substring(1);
		combo.removeAllItems();
		try {
			Method idGetter  = beanClass.getMethod(idGetterName);
			Method captionGetter = beanClass.getMethod(captionGetterName);
			
			for (Object o : collection) {
				Object id = idGetter.invoke(o);
				Object caption = captionGetter.invoke(o);
				if (caption == null) caption = "";
				Item item = combo.addItem(id);
				combo.setItemCaption(id, caption.toString());
			}
			
		} catch (Exception e) {
		}
	}
	
	public static void setComboBoxData (ComboBox combo, Collection collection){
		Converter<Object, Object> converter = combo.getConverter();
		if (converter instanceof BeanConverter) {
			BeanConverter bconverter = (BeanConverter)converter;
			Class modelClass = bconverter.getPresentationType();
			String idFieldName = bconverter.getIdFieldName();
			String captionFieldName = bconverter.getCaptionFieldName();
			combo.setConverter((Converter)null);
			setComboBoxData (combo, modelClass, collection, idFieldName, captionFieldName);
		}
	}
	
	public static boolean updateTabFocus (Object obj) {
		if (! (obj instanceof Component) ) return false;
		Component tab = (Component)obj; 
		
		if (! (tab instanceof AbstractComponentContainer) ) {
			if (tab instanceof Focusable) {
				((Focusable) tab).focus();
				return true;
			}
			return false;
		}
		Iterator<Component> iterator = ((AbstractComponentContainer)tab).getComponentIterator();
		Component c;
		while ( iterator.hasNext()) {
			c = iterator.next ();
			if (c instanceof Focusable) {
				((Focusable)c).focus();
				if (c instanceof TabSheet) {
					((TabSheet)c).setSelectedTab(0);
					Component selected = ((TabSheet)c).getSelectedTab();
					return updateTabFocus (selected);
				}
				else
				if (c instanceof Table) {
					Table t = (Table)c;
					if (t.getValue() != null)
						return true;
					
					Collection<?> ids = t.getItemIds();
					if (ids != null) {
						for (Object id : ids) {
							t.select(id);
							return true;
						}
					}
				}
				else
				if (c instanceof TextField) {
					TextField f = (TextField)c;
					f.selectAll();
				}
				
				return true;
			} else if (c instanceof AbstractComponentContainer) {
				if (updateTabFocus (c)) {
					return true;
				}
			}
		}
		return false;
	}
}
