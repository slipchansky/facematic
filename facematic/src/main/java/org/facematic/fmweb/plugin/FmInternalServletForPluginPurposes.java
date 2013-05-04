package org.facematic.fmweb.plugin;




import org.facematic.cdi.FmCdiServlet;
import org.facematic.core.ui.FacematicUI;


public class FmInternalServletForPluginPurposes extends FmCdiServlet {

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return FmAppUIforPluginPurposes.class; 
	}

}
