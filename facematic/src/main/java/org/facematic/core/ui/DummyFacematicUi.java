package org.facematic.core.ui;

import com.vaadin.server.VaadinRequest;

/**
 * For cases, where specific ui instance is not mandatory, for example,  for plugin purposes 
 * @author stas
 *
 */
public class DummyFacematicUi extends FacematicUI {
	@Override
	protected void init(VaadinRequest request) {
	}

}
