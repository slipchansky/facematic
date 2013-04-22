package com.slipchansky.fm.cdi;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class FmSession extends VaadinSession {
	
	public FmSession(VaadinService service) {
		super(service);
		addUIProvider(new DefaultUIProvider());
	}

	@Override
	public void addUIProvider(UIProvider uiProvider) {
		FmUiProvider fmUiProvider = new FmUiProvider (uiProvider);
		fmUiProvider.init (this);
		super.addUIProvider(fmUiProvider);
	}

	
	
	

}
