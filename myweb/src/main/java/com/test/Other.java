package com.test;

import com.vaadin.ui.UI;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import com.vaadin.ui.Component;
// GENERATED_CODE_BEGIN. Do not replace this comment!import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.annotations.FmController;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.ui.Html;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;


public class Other implements FmBaseController {
     @FmViewComponent(name="htmlControl")
     private Html htmlControl;

     @FmViewComponent(name="okButton")
     private Button okButton;

     @FmViewComponent(name="view")
     private VerticalLayout view;

// GENERATED_CODE_END. Do not replace this comment!
	
	@FmUI
	UI ui;
	
	
	
	@Override
	public void init () {
	   // TODO add your code here
	}
	
}

