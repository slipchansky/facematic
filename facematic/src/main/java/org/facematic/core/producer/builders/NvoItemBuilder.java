package org.facematic.core.producer.builders;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.facematic.core.nvo.Item;
import org.facematic.core.producer.FaceProducer;


public class NvoItemBuilder extends BeanBuilder {

	@Override
	public Class getBuildingClass() {
		return Item.class;
	}

	@Override
	public void build(FaceProducer builder, Object itemInstance, Element configuration) {
		String caption = configuration.attributeValue("value");
		
		if (caption==null) {
			caption = configuration.getText ();
			((Item)itemInstance).setValue(caption);
			return;
		}
		
		((Item)itemInstance).setValue(caption);
		
		List<Item> items = buildItems(builder, configuration);
		
		if (items != null && items.size() > 0)
		((Item)itemInstance).setNestedItems (items);
	}

	public static List<Item> buildItems(FaceProducer builder, Element configuration) {
		List<Item> items = null; 
		List<Element> nestedElements = configuration.elements("item");
		if (nestedElements != null) {
			items = new ArrayList<Item> ();
			for (Element nested : nestedElements) {
				try {
					Item nestedItem = builder.build(nested);
					items.add(nestedItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		}
		return items;
	}
}
