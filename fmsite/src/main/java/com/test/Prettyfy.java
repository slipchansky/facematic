package com.test;

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
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;


@FmView(name="com.test.Prettyfy")
public class Prettyfy implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(Prettyfy.class);
	
	@FmViewComponent
	Label prettyLabel;

    @Inject	
    @FmUI
    FacematicUI ui;
	
	/* (non-Javadoc)
	 * @see org.facematic.core.mvc.FmBaseController#prepareContext(org.facematic.core.producer.FaceProducer)
	 */
    @Override
	public void prepareContext(FaceProducer producer) {
	}
	
    @Override
    public void init () {
      JavaScript.getCurrent().execute("prettyPrint (function(){})");
    }
    
    public void pretty () {
        JavaScript.getCurrent().execute("prettyPrint (function(){})");
    }
	
}

