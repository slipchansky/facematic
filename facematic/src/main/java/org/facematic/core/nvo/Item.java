package org.facematic.core.nvo;

import java.util.ArrayList;
import java.util.List;

public class Item {
	
	String value;
	List<Item> nestedItems = new ArrayList<Item> ();

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return getValue ();
	}

	public List<Item> getNestedItems() {
		return nestedItems;
	}
	
	public void addNestedItem (Item nested) {
		nestedItems.add(nested);
	}

	public void setNestedItems(List<Item> items) {
		this.nestedItems = items;
	}
}
