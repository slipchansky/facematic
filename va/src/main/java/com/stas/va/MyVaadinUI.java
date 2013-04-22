package com.stas.va;


import javax.inject.Inject;


import com.slipchansky.fm.jit.JitController;
import com.slipchansky.fm.producer.FaceProducer;
import com.slipchansky.fm.ui.FacematicUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinUI extends FacematicUI {
	
	@Inject 
	private String mama;
	
	@Inject
	private Integer papa;
	
	public MyVaadinUI () {
		int k = 0;
		k++;
	}

    @Override
    protected void init(VaadinRequest request) {
    	
        final VerticalLayout layout = new VerticalLayout();
        
        layout.setMargin(true);
        layout.setSizeFull();
        setContent(layout);
        
    	TextField f;
    	JitController jitController = new JitController ();
    	FaceProducer  producer      = new FaceProducer(jitController, this);
    	Component content;
		try {
			
			content = producer.buildFromResource("com/slipchansky/fm/jit/JitView.xml");
			JitController controller = producer.getControllerInstance();
			controller.init ();
			setContent(content);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    }

}
