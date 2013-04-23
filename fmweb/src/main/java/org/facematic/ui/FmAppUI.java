package org.facematic.ui;

import com.slipchansky.fm.producer.FaceProducer;
import com.slipchansky.fm.ui.FacematicUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class FmAppUI extends FacematicUI {
	
    @Override
    protected void init(VaadinRequest request) {
    	

		try {
			FaceProducer  producer = new FaceProducer(this);
			Component content = producer.buildFromResource("org/facematic/ui/views/SampleView.xml"); //com/slipchansky/fm/jit/JitView.xml
			
			// Use next call for creating fm sandbox  
			// Component content =  producer.buildFromResource("com/slipchansky/fm/jit/JitView.xml"); 
			setContent(content);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
