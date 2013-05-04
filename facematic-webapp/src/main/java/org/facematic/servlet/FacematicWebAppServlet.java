package org.facematic.servlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.facematic.cdi.FmCdiServlet;
import org.facematic.core.ui.FacematicUI;
import org.facematic.ui.FacematicWebAppUI;


@WebServlet (urlPatterns = "/*")
public class FacematicWebAppServlet extends FmCdiServlet {

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return FacematicWebAppUI.class; 
	}

}
