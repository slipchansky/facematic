package org.facematic.site.showcase.complex;

import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;

import com.vaadin.ui.Notification;

public class ComplexTest1 implements FmBaseController {

	@Override
	public void prepareContext(FaceProducer producer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public void first () {
		Notification.show ("ComplexTest1.first");
	}

	public void second () {
		Notification.show ("ComplexTest1.second");
	}
	
}
