package org.facematic.fmweb.plugin;




import org.facematic.core.servlet.FacematicServlet;
import org.facematic.core.ui.FacematicUI;


/**
 * 
 * @author papa
 *
 */
public class FmInternalServletForPluginPurposes extends FacematicServlet {

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return FmAppUIforPluginPurposes.class; 
	}

}
