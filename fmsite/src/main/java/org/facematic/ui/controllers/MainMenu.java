package org.facematic.ui.controllers;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmController;
import org.facematic.core.annotations.FmSiblingControllers;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.ui.Component;


@FmView(name="org.facematic.ui.views.MainMenu")
public class MainMenu implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(MainMenu.class);

    @Inject	
    @FmUI
    FacematicUI ui;
    
    @FmSiblingControllers
    List<MenuItem> items;
    
    @FmController(name="parent")
    MainController parent;
    
    int currentItem = 0;
    

	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {
		for (int i=0; i<items.size(); i++) {
			items.get(i).setItemIndex (i);
		}
		items.get(0).select (true);
	}

	public void select(int index) {
		if (index == currentItem) return;
		items.get(currentItem).select (false);
		items.get(index).select (true);
		currentItem = index;
		parent.select (index);
	}
	
}

