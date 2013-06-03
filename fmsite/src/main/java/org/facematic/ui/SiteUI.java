package org.facematic.ui;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.annotations.FmUI;
import org.facematic.site.showcase.complex.ComplexTest;




@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet") 
@FmUI
@JavaScript ({"https://google-code-prettify.googlecode.com/svn/loader/prettify.js"})
@Theme("site")
public class SiteUI extends org.facematic.core.ui.FacematicUI {

    @Override
    protected void init(VaadinRequest request) {
		try {
			FaceProducer  producer = new FaceProducer(this);
			
		    	Object cont = producer.getViewFor (ComplexTest.class);
		    	if (cont != null) {
		    		setContent((Component)cont);
                        }
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

}