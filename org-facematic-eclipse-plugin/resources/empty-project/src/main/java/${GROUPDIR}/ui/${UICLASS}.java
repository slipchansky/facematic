package ${GROUPID}.ui;

import org.apache.log4j.Logger;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.annotations.FmUI;
import ${GROUPID}.ui.controllers.MainController;

@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet") 
@FmUI
@Theme("${THEMENAME}")
public class ${UICLASS} extends org.facematic.core.ui.FacematicUI {
	
	private static final Logger logger = LoggerFactory.getLogger (${UICLASS}.class);

	@Override
	protected void init (VaadinRequest request) {
		try {
			FaceProducer producer = new FaceProducer (this);
			Component content = producer.getViewFor (MainController.class);
			setContent (content);
		} catch (Exception e) {
			logger.error ("Could not instantinate UI content", e);
		}
	}
}