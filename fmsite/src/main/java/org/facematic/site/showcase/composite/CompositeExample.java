package org.facematic.site.showcase.composite;

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
import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;


@ShowCase(caption = "Composite", 
description = "The way to gather complex content from separate parts", 
moreClasses={
CompositeExampleNested.class, 
CompositeCustomController.class
}) 
@ShowFiles(java = true, xml = true)
@FmView(name="org.facematic.site.showcase.composite.CompositeExample")
public class CompositeExample  {


    public void onFirst () {
      Notification.show ("CompositeExample first clicked"); 
    }

    
    public void onSecond () {
        Notification.show ("CompositeExample second clicked"); 
      }
	
}

