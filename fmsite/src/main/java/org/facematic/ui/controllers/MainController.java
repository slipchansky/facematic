package org.facematic.ui.controllers;

import java.util.List;

import javax.inject.Inject;

import org.facematic.core.annotations.FmSiblingControllers;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.Component;
import org.facematic.core.ui.custom.Overlay;
import com.vaadin.ui.VerticalLayout;


@FmView(name="org.facematic.ui.views.MainView")
public class MainController implements FmBaseController {

    @Inject	
    @FmUI
    FacematicUI ui;
    
    
    @FmSiblingControllers
    List<MenuItem> items;

    @FmViewComponent(name="overlay")
    Overlay overlay;

    @FmViewComponent(name="view")
    VerticalLayout view;
    
	
    @Override
    public void init () {
      
    	
    }

    public void prepareContext (FaceProducer producer) {

    }

	public void select(int index) {
	   overlay.showElement(index);
	}

}

