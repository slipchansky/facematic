package org.facematic.fmweb.mvc.controllers;

import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;

import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@FmView(name="org.facematic.fmweb.mvc.views.SampleView")
public class SampleController implements FmBaseController {
	
    @FmViewComponent(name="tabSheet")
    private TabSheet tabSheet;

    @FmViewComponent(name="firstTab")
    private VerticalLayout firstTab;

    @FmViewComponent(name="secondTab")
    private VerticalLayout secondTab;

    @FmViewComponent(name="buttonFirst")
    private Button buttonFirst;

    @FmViewComponent(name="buttonSecond")
    private Button buttonSecond;

    @FmViewComponent(name="view")
    private VerticalLayout view;



    @SuppressWarnings("serial")
	@Override
    public void init () {
    	
    	
    	buttonFirst.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				tabSheet.setSelectedTab(firstTab);
			}
		});
    	
    	buttonSecond.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				tabSheet.setSelectedTab(secondTab);
			}
		});
    }

}
