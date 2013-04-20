package com.slipchansky.fm.jit;

import com.slipchansky.fm.mvc.annotations.FmViewComponent;
import com.slipchansky.fm.mvc.annotations.FmController;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;


public class SampleController {
     @FmViewComponent(name="okButton")
     private Button okButton;

     @FmViewComponent(name="cancelButton")
     private Button cancelButton;

     @FmViewComponent(name="view")
     private VerticalLayout view;

}
