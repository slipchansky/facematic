package org.facematic.site.showcase.simple;

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
import org.facematic.core.producer.FaceProducer;
import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload;


@FmView(name="org.facematic.site.showcase.simple.SimpleComponentsExample")
@ShowCase(caption = "Simple Face elements", description = "Labels, Buttons, ... etc.")
@ShowFiles(java = false, xml = true)
public class SimpleComponentsExample  {

}

