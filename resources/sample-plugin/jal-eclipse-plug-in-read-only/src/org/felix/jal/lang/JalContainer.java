package org.felix.jal.lang;

import java.util.ArrayList;
import java.util.List;

public class JalContainer extends JalElement {
	
	private List<JalElement> elements = new ArrayList<JalElement>();
	
	public JalContainer(String name) {
		super(name, name);
	}

	public JalContainer(String name, String typedName) {
		super(name, typedName);
	}
	
	public List<JalElement> getElements() {
		return elements;
	}
	public void addElement(JalElement elem) {
		elem.setParent(this);
		elements.add(elem);
	}
	
}
