package org.facematic.ui.controllers;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmController;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.core.ui.custom.Html;

import com.vaadin.ui.Component;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;


@FmView(name="org.facematic.ui.views.MenuItem")
public class MenuItem implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(MenuItem.class);

	private static final String PADDING_SIZE = "10px";

    @Inject	
    @FmUI
    FacematicUI ui;

    @FmViewComponent(name="button")
    Button button;

    @FmViewComponent(name="view")
    VerticalLayout view;
    
    @FmController 
    MainMenu parent;

	private String caption;
	private boolean isSelected;

	private int index;
	
    @Override
	public void prepareContext(FaceProducer producer) {
	}
	
    @Override
    public void init () {
        this.caption=button.getCaption();
        button.setHtmlContentAllowed(true);
        rebuild ();
    }
    
    public void select (boolean on) {
    	isSelected = on;
    	rebuild ();
    }

	private void rebuild() {
		String buttonCaption = "<h1 style='padding:"+PADDING_SIZE+";"+
	     "color:"+(isSelected?"#ccc;" : "#222;");
		 if (isSelected) buttonCaption+="background-color:"+(isSelected?"#222;" : "white;");
		 buttonCaption+= "'>"+caption+"</h1>";
		 button.setCaption(buttonCaption);
	}


	public void setItemIndex(int i) {
		index = i;
	}
	
	public void onClick () {
	    parent.select (index);
	}
	
}

