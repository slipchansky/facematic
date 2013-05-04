package org.facematic.fmweb.plugin;

import org.facematic.core.producer.FaceProducer;

import com.vaadin.annotations.Widgetset;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("reindeer")
@Widgetset("com.vaadin.DefaultWidgetSet") 
public class FmAppUIforPluginPurposes extends org.facematic.core.ui.FacematicUI {
	
    @Override
    protected void init(VaadinRequest request) {
		try {
			FaceProducer  producer = new FaceProducer(this);
			
			String viewName = request.getPathInfo();
		    if (viewName.startsWith("/")) {
		        viewName = viewName.substring(1);
		    }
		    Component content = new Label ("No View");
		    
		    if (!viewName.equals("")) {
		    	Object cont = producer.buildFromResource(viewName); //com/slipchansky/fm/jit/JitView.xml
		    	if (cont != null)
		    		content = (Component)cont;
		    }
			
			
			setContent(content);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
