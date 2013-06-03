package org.facematic.site.showcase.complex;

import com.vaadin.ui.Notification;

public class ComplexSecond extends Complex {

	@Override
	public void first() {
	   Notification.show("ComplexSecond.first");
	}

	@Override
	public void second() {
		   Notification.show("ComplexSecond.second");
	}
	
	public void third () {
		Notification.show("ComplexSecond.third");
	}
	

}
