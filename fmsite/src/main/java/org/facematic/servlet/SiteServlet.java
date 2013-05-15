package org.facematic.servlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.facematic.core.servlet.FacematicServlet;
import org.facematic.core.ui.FacematicUI;

import org.facematic.ui.SiteUI;


@WebServlet (urlPatterns = "/*")
public class SiteServlet extends FacematicServlet {

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return SiteUI.class; 
	}

}
