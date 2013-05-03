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
import com.vaadin.ui.Button;
import org.facematic.core.ui.Composite;
import com.test.Other;
import org.facematic.core.ui.Html;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;


public class MyControl implements FmBaseController {
     @FmViewComponent(name="cbOk")
     private Button cbOk;

     @FmViewComponent(name="cbCancel")
     private Button cbCancel;

     @FmViewComponent(name="nested")
     private Composite nested;

     @FmController(name="nested")
     private Other nestedController;

     @FmViewComponent(name="nested.htmlControl")
     private Html nested_htmlControl;

     @FmViewComponent(name="nested.view")
     private VerticalLayout nested_view;

     @FmViewComponent(name="view")
     private HorizontalLayout view;

// GENERATED_CODE_END. Do not replace this comment!

	@FmUI
	UI ui;

	@Override
	public void init() {
		cbOk.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				//nested_htmlControl.setVisible(false);
				cbCancel.setCaption("Dada");
				nested_htmlControl.setCaption("Mama myla ramu!!!");
				nested_htmlControl.setValue("newStringValue");
			}
		});
	}

}
