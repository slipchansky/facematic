package com.test;

import org.facematic.core.annotations.FmUI;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;

public class SecondController implements FmBaseController{
	@FmUI
	FacematicUI ui;

	@Override
	public void prepareContext(FaceProducer producer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		ui = ui;
		
	}
	
	public void first () {
		ui.showNotification("Here we are");
	}

}
