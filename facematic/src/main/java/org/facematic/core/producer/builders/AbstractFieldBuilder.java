package org.facematic.core.producer.builders;

import java.lang.reflect.Method;

import org.dom4j.Element;
import org.facematic.core.producer.FaceProducer;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field.ValueChangeEvent;

public class AbstractFieldBuilder extends ComponentBuilder {

	@Override
	public Class getBuildingClass() {
		return AbstractField.class;
	}

	@Override
	public void build(FaceProducer builder, final Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		String onChangeMethodName = configuration.attributeValue("onChange");
		
		final Object controller = builder.getControllerInstance();
		
		if (onChangeMethodName != null && controller != null) {
			
			builder.addMethod(onChangeMethodName, ValueChangeEvent.class);
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
							// skip
						}	
					} catch (Exception ee) {
						// skip
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
