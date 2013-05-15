package org.facematic.core.producer.builders;

import java.util.List;

import org.facematic.core.nvo.Item;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Tree;

public class TreeBuilder extends SelectBuilder {

	@Override
	public Class getBuildingClass() {
		return Tree.class;
	}

	@Override
	protected void addContainerDataSource(AbstractSelect select, List<Item> items) {
		HierarchicalContainer container = new HierarchicalContainer();
		Tree tree = (Tree)select;
		
		Object itemCaptionPropertyId = tree.getItemCaptionPropertyId(); 
		if (itemCaptionPropertyId==null) itemCaptionPropertyId="value";
		container.addContainerProperty(itemCaptionPropertyId, String.class, null);
		builContainer (container, itemCaptionPropertyId, -1, items);
		select.setContainerDataSource(container);
	}
	
	private int builContainer (HierarchicalContainer container, Object itemCaptionPropertyId, int parentItemId, List<Item> items) {
		
		int itemId = parentItemId+1;
		if (items == null || items.size()==0) {
			return itemId;
		}
		for (Item i : items) {
			   com.vaadin.data.Item item = container.addItem(itemId);
			   container.setParent(itemId, parentItemId);
			   item.getItemProperty(itemCaptionPropertyId).setValue(i.toString());
			   if (i.getNestedItems() != null && i.getNestedItems().size() != 0) {
				   container.setChildrenAllowed(itemId, true);
				   itemId = builContainer (container, itemCaptionPropertyId, itemId, i.getNestedItems ());
			   }
			   else { 
				   container.setChildrenAllowed (itemId, false);
				   itemId++;
			   }
		}
		return itemId;
	}
	
}
