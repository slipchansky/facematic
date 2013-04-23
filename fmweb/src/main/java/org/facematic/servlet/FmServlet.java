package org.facematic.servlet;



import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.facematic.ui.FmAppUI;
import com.slipchansky.fm.cdi.FmCdiServlet;
import com.slipchansky.fm.ui.FacematicUI;

@WebServlet (urlPatterns = "/*")
public class FmServlet extends FmCdiServlet {

    // put your external injections here
	

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return FmAppUI.class; 
	}

}
