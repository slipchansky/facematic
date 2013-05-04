package ${GROUPID}.servlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.facematic.cdi.FmCdiServlet;
import org.facematic.core.ui.FacematicUI;
import ${GROUPID}.ui.${UICLASS};


@WebServlet (urlPatterns = "/*")
public class ${SERVLETCLASS} extends FmCdiServlet {

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return ${UICLASS}.class; 
	}

}
