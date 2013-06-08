package org.facematic.site.showcase.overlay;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.facematic.core.ui.custom.Overlay;
import org.facematic.core.ui.custom.Overlay.SelectedElementChangeEvent;

@FmView(name = "org.facematic.site.showcase.overlay.OverlayExample")
public class OverlayExample implements FmBaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(OverlayExample.class);

	@Inject
	@FmUI
	FacematicUI ui;

	@FmViewComponent(name = "selector")
	ComboBox selector;

	@FmViewComponent(name = "overlay")
	Overlay overlay;

	@FmViewComponent(name = "view")
	VerticalLayout view;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.mvc.FmBaseController#prepareContext(org.facematic.
	 * core.producer.FaceProducer)
	 */
	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {

        overlay.setUi (ui);
		for (int i = 0; i < overlay.getElementsCount(); i++) {
			selector.addItem(i);
			selector.setItemCaption(i, overlay.getElementCaption(i));
		}

		selector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Integer elementPosition = (Integer) event.getProperty().getValue();
				showElement(elementPosition);
			}
		});
		
		selector.setValue (0);
	}


    @FmReaction("Overlay#overlay.onElementChange")
    public void elementChanged (SelectedElementChangeEvent event) {
        logger.trace ("element N "+event.getElementPosition());
        selector.setValue(event.getElementPosition());
    }
    
    private void showElement(Integer elementPosition) {
		overlay.showElement(elementPosition);
	}

}
