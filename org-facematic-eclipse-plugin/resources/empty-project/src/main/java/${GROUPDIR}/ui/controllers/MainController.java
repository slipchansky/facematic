package ${GROUPID}.ui.controllers;

import javax.inject.Inject;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.ui.Component;

${GENERATED_CODE_BEGIN}


@FmView(name="${GROUPID}.ui.views.MainView")
public class MainController implements FmBaseController {
	
${GENERATED_CODE_END}

    @Inject	
    @FmUI
    FacematicUI ui;
	
	@Override
	public void init () {
	   // TODO add your code here
	}
	
}

