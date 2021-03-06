package org.facematic.core.producer.builders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.utils.VelocityEngine;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field.ValueChangeEvent;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class ButtonBuilder extends ComponentBuilder {
	private final static Logger logger = LoggerFactory.getLogger(ButtonBuilder.class);
	
	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return Button.class;
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object viewInstance,
			Element configuration) {
		super.build(builder, viewInstance, configuration);
		
		String producerName = configuration.attributeValue("name");
		String producerCaption = configuration.attributeValue("caption");

		String onClickMethodName = configuration.attributeValue("onClick");
		
		if (onClickMethodName != null) {
		    addOnClickCall(builder, viewInstance, onClickMethodName, producerName, producerCaption);
		}

	}

	/**
	 * @param builder
	 * @param viewInstance
	 * @param onClickMethodName
	 * @param producerName
	 * @param producerCaption
	 */
	private void addOnClickCall(FaceProducer builder,
			final Object viewInstance, String onClickMethodName, String producerName, String producerCaption) {
		builder.setListener(onClickMethodName, ClickEvent.class, viewInstance.getClass(), producerName, producerCaption, "onClick");
		
		final Object controller = builder.getControllerInstance();
		
		if (onClickMethodName != null && controller==null) {
			logger.warn("There is no controller class for implementing listener method "+onClickMethodName);
		}
		
		if(controller==null) {
			return;
		}
		
		
		// try to add clickListener (ClickEvent event) 
		try {
			final Method onClickMethod = controller.getClass().getMethod(
					onClickMethodName, Button.ClickEvent.class);

			((Button) viewInstance)
					.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							try {
								onClickMethod.invoke(controller, event);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

		} catch (NoSuchMethodException nsm) {
			
			// try to add clickListener ()
			logger.warn("Method "+controller.getClass()+"."+onClickMethodName+" (Button.ClickEvent) not found");
			try {
			final Method onClickMethod = controller.getClass().getMethod(onClickMethodName);
			((Button) viewInstance).addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							try {
								onClickMethod.invoke(controller);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			} catch (Exception ee) {
				logger.warn("Method "+controller.getClass()+"."+onClickMethodName+" () not found");
			}
		} catch (Exception e) {
			logger.warn("Unexpected exception on using "+controller.getClass()+"."+onClickMethodName+" (...)", e);
		}

	}

}
