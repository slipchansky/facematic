package org.facematic.site.showcase.composite;

import org.facematic.core.annotations.FmController;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@ShowFiles(java = true, xml = false)
public class CompositeCustomController extends CompositeExampleNested implements FmBaseController {
	
	@FmController(name="parent")
	CompositeExample parent;
	
	@FmViewComponent
	Button demonstrateParentCallButton;
	
	
	@Override
	public void onFirst() {
		Notification.show("CompositeCustomController first clicked", Type.ERROR_MESSAGE);
	}
	
	@FmReaction("demonstrateParentCallButton.onClick")
	public void callToParent () {
		parent.showSibling();
	}

	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {
		demonstrateParentCallButton.setVisible(true);
		
	}

}
