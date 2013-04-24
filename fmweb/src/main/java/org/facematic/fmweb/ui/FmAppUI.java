package org.facematic.fmweb.ui;

import org.facematic.core.producer.FaceProducer;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("fmweb")
public class FmAppUI extends org.facematic.core.ui.FacematicUI {
	
    @Override
    protected void init(VaadinRequest request) {
		try {
			FaceProducer  producer = new FaceProducer(this);
			Component content = producer.buildFromResource("org/facematic/fmweb/mvc/views/SampleView.xml"); //com/slipchansky/fm/jit/JitView.xml
			
			// Use next call for creating fm sandbox  
			// Component content =  producer.buildFromResource("com/slipchansky/fm/jit/JitView.xml"); 
			setContent(content);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
