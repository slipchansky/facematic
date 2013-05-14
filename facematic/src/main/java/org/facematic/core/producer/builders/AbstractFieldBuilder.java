package org.facematic.core.producer.builders;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.producer.FaceReflectionHelper;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field.ValueChangeEvent;

public class AbstractFieldBuilder extends ComponentBuilder {
	private static Logger logger = LoggerFactory.getLogger(AbstractFieldBuilder.class);

	@Override
	public Class getBuildingClass() {
		return AbstractField.class;
	}

	@Override
	public void build(FaceProducer builder, final Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		String onChangeMethodName = configuration.attributeValue("onChange");
		String producerName = configuration.attributeValue("name");
		String producerCaption = configuration.attributeValue("caption");
		
		final Object controller = builder.getControllerInstance();
		
		if (onChangeMethodName != null && controller == null) {
			logger.warn("There is no controller class for implementing listener method "+onChangeMethodName);
		}
		
		if (onChangeMethodName != null && controller != null) {
			
			builder.setListener(onChangeMethodName, ValueChangeEvent.class, oComponent.getClass(), producerName, producerCaption, "onChange");
			// Try to add listenerMethod (ValueChangeEvent event); 
			try {
				final Method onChangeMethod = controller.getClass().getMethod(onChangeMethodName, ValueChangeEvent.class);
				((AbstractField)oComponent).addValueChangeListener(new Property.ValueChangeListener() {
					@Override
					public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
						try {
							onChangeMethod.invoke(controller, event);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} 
			catch (NoSuchMethodException e) {
				logger.info("Method "+controller.getClass()+"."+onChangeMethodName+" (ValueChangeEvent) not found");
				// Try to add listenerMethod (Property property);
				try {
					final Method onChangeMethod = controller.getClass().getMethod(onChangeMethodName, Property.class);
					((AbstractField)oComponent).addValueChangeListener(new Property.ValueChangeListener() {
						@Override
						public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
							try {
								onChangeMethod.invoke(controller, event.getProperty());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch (NoSuchMethodException nsm) {
					logger.info("Method "+controller.getClass()+"."+onChangeMethodName+" (Property) not found");
					// Try to add listenerMethod ();
					
					try {
						try {
							final Method onChangeMethod = controller.getClass().getMethod(onChangeMethodName);
							((AbstractField)oComponent).addValueChangeListener(new Property.ValueChangeListener() {
								@Override
								public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
									try {
										onChangeMethod.invoke(controller);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}catch (Exception eee) {
							logger.info("Method "+controller.getClass()+".onChangeMethodName () not found");
						}	
					} catch (Exception ee) {
						logger.warn("Unexpeted exception when trying to add Listener "+controller.getClass()+".onChangeMethodName", ee);
					}
				}
			}
			catch (Exception e) {
				logger.warn("Unexpeted exception when trying to use Listener "+controller.getClass()+".onChangeMethodName", e);
			}
		}
	}
}
