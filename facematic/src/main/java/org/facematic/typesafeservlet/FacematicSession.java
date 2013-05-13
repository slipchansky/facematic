package org.facematic.typesafeservlet;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class FacematicSession extends VaadinSession {
	
	public FacematicSession(VaadinService service) {
		super(service);
	}

	@Override
	public void addUIProvider(UIProvider uiProvider) {
		FacematicDefaultUiProvider fmUiProvider = new FacematicDefaultUiProvider (uiProvider);
		fmUiProvider.init (this);
		super.addUIProvider(fmUiProvider);
	}

	
	
	

}
