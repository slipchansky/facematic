package org.facematic.site.showcase.controllers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.ui.FacematicUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload.StartedEvent;
import org.facematic.core.nvo.UploadProgressEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload;


@FmView(name="org.facematic.site.showcase.views.SimpleComponents")
public class SimpleComponents implements FmBaseController {
    static final Logger logger = LoggerFactory.getLogger(SimpleComponents.class);

    @Inject	
    @FmUI
    FacematicUI ui;

    @FmViewComponent(name="view")
    VerticalLayout view;

    @FmViewComponent(name="upload")
    Upload upload;
	
	
	
	@Override
	public void init () {
	
	}
	

    @FmReaction("Upload[caption='Upload'].onStarted")
    public void onStarted (StartedEvent event) {
    com.vaadin.ui.JavaScript.getCurrent().execute("alert('A'); prettyPrint (); alert('B')");
     logger.info(event.getFilename()); 
    }

    @FmReaction("Upload[caption='Upload'].onProgress")
    public void onProgress (UploadProgressEvent event) {
     logger.info("progress: "+event.getReadBytes()+"("+ event.getReadBytes()+")"); 
    }

    @FmReaction("Upload[caption='Upload'].onSucceeded")
    public void onSucceeded (SucceededEvent event) {
    	logger.info("onSucceeded:"+event.getFilename()); 
    }

    @FmReaction("Upload[caption='Upload'].onFailed")
    public void onFailed (FailedEvent event) {
    	logger.info("onFailed:"+event.getFilename());
    }

    @FmReaction("Upload[caption='Upload'].onFinished")
    public void Finished (StartedEvent event) {
    logger.info("Upload finished");
     int k = 0;
     k++; 
    }
}

