package org.facematic.fmweb.servlet;



import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

import org.facematic.cdi.FmCdiServlet;
import org.facematic.core.ui.FacematicUI;
import org.facematic.fmweb.ui.FmAppUI;


@WebServlet (urlPatterns = "/*")
public class FmInternalServletForPluginPurposes extends FmCdiServlet {

	

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return FmAppUI.class; 
	}

}
