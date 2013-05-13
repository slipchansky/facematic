package ${GROUPID}.servlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.facematic.core.servlet.FacematicServlet;
import org.facematic.core.ui.FacematicUI;

import ${GROUPID}.ui.${UICLASS};


@WebServlet (urlPatterns = "/*")
public class ${SERVLETCLASS} extends FacematicServlet {

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return ${UICLASS}.class; 
	}

}
