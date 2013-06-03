package org.facematic.site.showcase.viewer;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.site.showcase.complex.ComplexExample;
import org.facematic.utils.StreamUtils;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;


@FmView(name="org.facematic.site.showcase.viewer.ShowCaseViewer")
public class ShowCaseViewer implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ShowCaseViewer.class);

    @Inject	
    @FmUI
    FacematicUI ui;

    @FmViewComponent(name="showCaseViewer")
    TabSheet showCaseViewer;

    @FmViewComponent(name="view")
    VerticalLayout view;

	private Class mainClass;
    
    public ShowCaseViewer (Class mainClass) {
       this.mainClass = mainClass;
    }
	
    @Override
	public void prepareContext(FaceProducer producer) {
	}
	
    @Override
    public void init () {
       String javaCodeName = mainClass.getCanonicalName().replace('.', '/')+".java";
       String source = StreamUtils.getResourceAsString(javaCodeName); 
       
    
    }
	
    
}

