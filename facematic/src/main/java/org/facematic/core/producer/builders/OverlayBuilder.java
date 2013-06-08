package org.facematic.core.producer.builders;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Overlay;
import org.facematic.core.ui.custom.Overlay.SelectedElementChangeEvent;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Window;

public class OverlayBuilder extends ComponentContainerBuilder {
	private final static Logger logger = LoggerFactory
			.getLogger(OverlayBuilder.class);

	@Override
	public Class getBuildingClass() {
		return Overlay.class;
	}

	@Override
	public void build(FaceProducer builder, Object viewInstance,
			Element configuration) {
		

		String onChangeMethodName = configuration
				.attributeValue("onElementChange");
		if (onChangeMethodName == null || "".equals(onChangeMethodName.trim())) {
			super.build(builder, viewInstance, configuration);
			return;
		}

		String producerName = configuration.attributeValue("name");
		String producerCaption = configuration.attributeValue("caption");

		addOnChangeCall(builder, viewInstance, onChangeMethodName, producerName, producerCaption);
		super.build(builder, viewInstance, configuration);

	}

	private void addOnChangeCall(FaceProducer builder, Object viewInstance,
			String onChangeMethodName, String producerName,
			String producerCaption) {

		builder.setListener(onChangeMethodName,
				Overlay.SelectedElementChangeEvent.class,
				viewInstance.getClass(), producerName, producerCaption,
				"onElementChange");

		final Object controller = builder.getControllerInstance();

		if (onChangeMethodName != null && controller == null) {
			logger.warn("There is no controller class for implementing listener method "
					+ onChangeMethodName);
		}

		if (controller == null) {
			return;
		}

		final Overlay overlay = (Overlay) viewInstance;
		try {
			final Method onElementChangeMethod = controller.getClass()
					.getMethod(onChangeMethodName,
							Overlay.SelectedElementChangeEvent.class);
			overlay.addSelectedElementChangeListener(new Overlay.SelectedElementChangeListener() {
				@Override
				public void selectedElementChange(
						SelectedElementChangeEvent event) {
					try {
						onElementChangeMethod.invoke(controller, event);
					} catch (Exception e) {
						logger.error("Can't process overlay element changing "
								+ onElementChangeMethod, e);
					}
				}
			});
		} catch (NoSuchMethodException nsm) {
			logger.warn("Method " + controller.getClass() + "."
					+ onChangeMethodName
					+ " (Overlay.SelectedElementChangeEvent) not found");
			try {
				final Method onElementChangeMethod = controller.getClass()
						.getMethod(onChangeMethodName);
				overlay.addSelectedElementChangeListener(new Overlay.SelectedElementChangeListener() {
					@Override
					public void selectedElementChange(
							SelectedElementChangeEvent event) {
						try {
							onElementChangeMethod.invoke(controller);
						} catch (Exception e) {
							logger.error(
									"Can't process overlay element changing "
											+ onElementChangeMethod, e);
						}
					}
				});
			} catch (Exception e) {
				logger.warn("Method " + controller.getClass() + "."
						+ onChangeMethodName + " () not found");
				return;
			}
		}
	}

	protected void addComponent(AbstractComponentContainer container,
			AbstractComponent component, Element node) {
		try {
			container.addComponent(component);
		} catch (Exception e) {
			logger.error("Cant add component of class "
					+ component.getClass().getCanonicalName()
					+ " to Overlay");
		}
	}

}
