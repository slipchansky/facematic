package org.facematic.core.ui.custom;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class Html extends Label {
	
	public Html () {
		setContentMode(ContentMode.HTML);
	}

}
