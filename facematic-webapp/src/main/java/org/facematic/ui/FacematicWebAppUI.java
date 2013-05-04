package org.facematic.ui;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import org.facematic.core.producer.FaceProducer;




@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet") 
public class FacematicWebAppUI extends org.facematic.core.ui.FacematicUI {

    @Override
    protected void init(VaadinRequest request) {
		try {
			FaceProducer  producer = new FaceProducer(this);
			
		    	Object cont = producer.buildFromResource("org.facematic.ui.views.MainView");
		    	if (cont != null) {
		    		setContent((Component)cont);
                        }
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

}