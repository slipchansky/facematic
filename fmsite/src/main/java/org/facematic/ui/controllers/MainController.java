package org.facematic.ui.controllers;

import javax.inject.Inject;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.Component;


@FmView(name="org.facematic.ui.views.MainView")
public class MainController implements FmBaseController {

    @Inject	
    @FmUI
    FacematicUI ui;
	
    @Override
    public void init () {
      // TODO add your code here
    	////ui.getJa executeJavaScript ("");
    	
    }
	
}

