package org.facematic.fmweb.plugin;




import org.facematic.core.ui.FacematicUI;
import org.facematic.typesafeservlet.FacematicServlet;


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
