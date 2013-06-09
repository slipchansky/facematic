package org.facematic.site.showcasestudio;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.annotations.ShowFiles;
import org.facematic.site.showcase.viewer.ShowCaseViewer;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import org.facematic.core.ui.custom.Overlay;

import com.vaadin.ui.VerticalLayout;

@FmView(name = "org.facematic.site.showcasestudio.ShowCaseStudio")
@ShowCase(part = ShowCase.EXAMPLES_OF_USE, caption = "ShowCase studio", description = "How does the Show-Case studio made", moreClasses = {
		ShowCaseCollection.class, ShowCase.class, ShowFiles.class })
@ShowFiles(java = true, xml = true, sample = false)
public class ShowCaseStudio implements FmBaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(ShowCaseStudio.class);

	@Inject
	@FmUI
	FacematicUI ui;

	@FmViewComponent(name = "mainSelector")
	Tree selector;

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

		Component selected = null;
		selector.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		selector.setImmediate(true);

		for (Class<?> c : ShowCaseCollection.getCases()) {
			final ShowCase showCaseAnnotation = (ShowCase) c
					.getAnnotation(ShowCase.class);
			if (showCaseAnnotation == null) {
				continue;
			}

			final String caption = showCaseAnnotation.caption();
			final String description = showCaseAnnotation.description();
			final String part = showCaseAnnotation.part();

			try {
				ShowCaseViewer viewerController = new ShowCaseViewer(c);
				FaceProducer producer = new FaceProducer(viewerController, ui);
				Component view = producer.getViewFor(ShowCaseViewer.class);
				view.setCaption(description);
				overlay.addElement(view);

				if (selector.getItem(part) == null) {
					selector.addItem(part);
					selector.setItemCaption(part, part);
					selector.setChildrenAllowed(part, true);
					selector.expandItem(part);
					//selector.set
				}
				selector.addItem(view);
				selector.setItemCaption(view, caption);
				selector.setParent(view, part);
				selector.setChildrenAllowed(view, false);

				selector.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
					@Override
					public String generateDescription(Component source,
							Object itemId, Object propertyId) {
						if (itemId instanceof Component)
							return overlay
									.getElementCaption((Component) itemId);
						else
							return "" + itemId;
					}
				});
			} catch (Exception e) {
				logger.error("error", e);
			}
		}

		selector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue() instanceof Component)
					showCase((Component) event.getProperty().getValue());
			}
		});

		if (overlay.getElementsCount() != 0) {
			selector.setValue(overlay.getElement(0));
		}
	}

	/**
	 * Fires on tree selection change event
	 * 
	 * @param element
	 */
	protected void showCase(Component element) {
		overlay.showElement(element);
	}

}
