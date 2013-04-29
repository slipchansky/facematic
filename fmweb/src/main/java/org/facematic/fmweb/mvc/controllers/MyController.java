package org.facematic.fmweb.mvc.controllers;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;


@FmView(name="org.facematic.fmweb.mvc.views.MyView")
public class MyController implements FmBaseController {

	@FmUI
	UI ui;
	
	@Override
	public void init () {
	   // TODO add your code here
	}
	
}

