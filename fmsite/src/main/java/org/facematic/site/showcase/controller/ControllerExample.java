package org.facematic.site.showcase.controller;

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
import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

@FmView(name = "org.facematic.site.showcase.controller.ControllerExample")
@ShowFiles(java = true, xml = true)
@ShowCase(part=ShowCase.FACEMATIC_BASICS, caption = "Controller", description = "A method of controlling the behavior of view elements")
public class ControllerExample implements FmBaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(ControllerExample.class);

	@Inject
	@FmUI
	FacematicUI ui;

	@FmViewComponent(name = "theTextField")
	TextField theTextField;

	@FmViewComponent(name = "theButton")
	Button theButton;

	@FmViewComponent(name = "oneMoreButton")
	Button oneMoreButton;

	@FmViewComponent(name = "view")
	VerticalLayout view;

	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {
		theTextField.setValue("Enter text here");

		oneMoreButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showEnteredText(theTextField.getValue());

			}
		});
	}

	@FmReaction("Button#theButton.onClick")
	public void theButtonClicked() {
		showEnteredText(theTextField.getValue());
	}

	private void showEnteredText(String value) {
		Notification.show(value, Type.TRAY_NOTIFICATION);
	}

}
