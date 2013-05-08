package org.facematic.utils;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

public class VaadinUtil {
	
	/**
	 * Checks if the component is alive
	 * @param component
	 * @return
	 */
	public static boolean isViewAlive (Component component) {
		if (component instanceof UI) {
			return true;
		}
		Component parent = component.getParent ();
		if (parent==null) {
			return false;
		}
		return isViewAlive (parent);
	}
	
}
