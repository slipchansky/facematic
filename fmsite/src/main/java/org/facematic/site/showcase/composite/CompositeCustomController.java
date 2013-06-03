package org.facematic.site.showcase.composite;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class CompositeCustomController extends CompositeExampleNested {

	@Override
	public void onFirst() {
		Notification.show("CompositeCustomController first clicked", Type.ERROR_MESSAGE);
	}

}
