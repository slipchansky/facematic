package com.stas.va;


import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;

import com.slipchansky.fm.cdi.FmCdiServlet;
import com.slipchansky.fm.ui.FacematicUI;

@WebServlet(urlPatterns = "/*")
//initParams = {   
//        @WebInitParam(name="UI", value="com.stas.va.MyVaadinUI"),  
//        @WebInitParam(name="widgetset", value="com.stas.va.AppWidgetSet"),  
//        @WebInitParam(name="productionMode", value="false")  
//})
public class VaServlet extends FmCdiServlet {
	
	@Inject 
	private String mama = "mama mrla ramyu";
	
	@Inject
	private Integer papa=124;
	
	

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return MyVaadinUI.class; 
	}

}
