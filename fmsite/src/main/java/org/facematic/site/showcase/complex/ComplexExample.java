package org.facematic.site.showcase.complex;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.site.showcase.annotations.ShowCase;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;


@ShowCase (
caption = "Complex", 
description = "Te way to describe most of the parts of the presentation in one place and build a composite of these items")
@FmView(name="org.facematic.site.showcase.complex.ComplexExample")
public class ComplexExample {
    private static final Logger logger = LoggerFactory.getLogger(ComplexExample.class);
    
    public static class Sub extends ComplexExample {
		public void onSecond () {
		   Notification.show("Sub.second");
		}
		
		public void onThird () {
		   Notification.show("Sub.third");
		}
    }
    
	public void onFirst () {
	  Notification.show("ComplexExample.first");
	}
	
	public void onSecond () {
	  Notification.show("ComplexExample.second");
	}
	
}

