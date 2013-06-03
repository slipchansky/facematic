package org.facematic.site.showcase.viewer;

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
import org.facematic.core.ui.custom.Html;
import org.facematic.utils.StreamUtils;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@FmView(name = "org.facematic.site.showcase.viewer.SourceViewer")
public class SourceViewer implements FmBaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(SourceViewer.class);

	@Inject
	@FmUI
	FacematicUI ui;

	@FmViewComponent(name = "xmlCode")
	TextArea xmlCode;

	@FmViewComponent(name = "javaCode")
	TextArea javaCode;
	
	@FmViewComponent(name = "xmlCaption")
	Html xmlCaption;
	
	@FmViewComponent(name = "javaCaption")
	Html javaCaption;
	

	@FmViewComponent(name = "view")
	VerticalLayout view;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.mvc.FmBaseController#prepareContext(org.facematic.
	 * core.producer.FaceProducer)
	 */
	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {
		Class mainClass = this.getClass();
		String javaCodeName = mainClass.getCanonicalName().replace('.', '/')+ ".java";
		String xmlCodeName = null;
		String xml = null;
		String source = StreamUtils.getResourceAsString(javaCodeName);

		if (source != null) {
			FmView fmViewAnnotation = (FmView) mainClass
					.getAnnotation(FmView.class);
			if (fmViewAnnotation != null) {
				xmlCodeName = fmViewAnnotation.name();
				xmlCodeName = xmlCodeName.replace('.', '/') + ".xml";
				xml = StreamUtils.getResourceAsString(xmlCodeName);
			}
		} else {
			xmlCodeName = mainClass.getCanonicalName().replace('.', '/')+ ".xml";
			xml = StreamUtils.getResourceAsString(xmlCodeName);
		}

		if (source != null) {
		   javaCode.setValue(source);
		   javaCode.setReadOnly(true);
		   javaCaption.setValue("<h2>"+mainClass.getCanonicalName()+"</h2>");
		} else {
			javaCode.setVisible(false);
			xmlCode.setCaption(null);
		}
		if (xml != null) {
			xmlCode.setValue(xml);
			xmlCode.setReadOnly(true);
			xmlCaption.setValue("<h2>"+xmlCodeName+"</h2>");
		} else {
			xmlCode.setVisible(false);
			javaCode.setCaption(null);
		}
			
	}

}
