package org.facematic.core.producer.builders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import org.facematic.utils.GroovyEngine;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field.ValueChangeEvent;

public class ButtonBuilder extends ComponentBuilder {
	@Override
	public Class getBuildingClass() {
		return Button.class;
	}

	@Override
	public void build(FaceProducer builder, Object viewInstance,
			Element configuration) {
		super.build(builder, viewInstance, configuration);

		Element onClickNode = configuration.element("onClick");

		// TODO Переделать(или расширить) с мепингом события на метод
		// контроллера

		if (onClickNode != null) {

			String onClickMethodName = onClickNode.attributeValue("call");
			if (onClickMethodName != null) {
				addOnClickCall(builder, viewInstance, onClickMethodName);
			}

			final String onClickScript = onClickNode.getText();

			final GroovyEngine engine = builder.getGroovyEngine();
			Button button = (Button) viewInstance;
			button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					engine.evaluate(onClickScript);
				}
			});
		}

	}

	private void addOnClickCall(FaceProducer builder,
			final Object viewInstance, String onClickMethodName) {
		builder.addMethod(onClickMethodName, ClickEvent.class);
		
		final Object controller = builder.getControllerInstance();
		
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
				// skip;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
