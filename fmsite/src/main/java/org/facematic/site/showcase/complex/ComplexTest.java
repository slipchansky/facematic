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
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;


@FmView(name="org.facematic.site.showcase.complex.ComplexTest")
public class ComplexTest implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ComplexTest.class);

    @Inject	
    @FmUI
    FacematicUI ui;
	
	/* (non-Javadoc)
	 * @see org.facematic.core.mvc.FmBaseController#prepareContext(org.facematic.core.producer.FaceProducer)
	 */
    @Override
	public void prepareContext(FaceProducer producer) {
    	// Here you can define context environment before producer will prepare your component set.
    	// Remember! Your view components not created yet!
    	//
    	// examle:
    	// producer.put("firstName", someValue);
    	// producer.putAll(variablesMap);
    	//
    	// Then in view you can use expressions like:
    	//
    	// <html>
    	// Hello, ${firstName} !
    	// </html>
    	//
		// TODO add your code here
	}
	
    @Override
    public void init () {
        // Your view components already created.
    	// Here you can add some specific initialization code. For example, you could add the listeners to you components.
    	//
    	// Example:
    	//
    	// myButton.addClickListener (new Button.ClickListener () { ... }); 
    	//
    	// TODO add your code here
    }
	
    public static void main(String[] args) throws Exception {
		FaceProducer fp = new FaceProducer ();
		fp.getViewFor(ComplexTest.class);
	}
	
	public void first () {
	  Notification.show("ComplexTest.first");
	}
	
	public void second () {
	  Notification.show("ComplexTest.second");
	}
	
}

