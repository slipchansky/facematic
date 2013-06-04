package org.facematic.site.showcase.composite;

import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@ShowFiles(java = true, xml = false)
public class CompositeCustomController extends CompositeExampleNested {
	@Override
	public void onFirst() {
		Notification.show("CompositeCustomController first clicked", Type.ERROR_MESSAGE);
	}

}
