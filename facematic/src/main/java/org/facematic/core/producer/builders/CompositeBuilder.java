package org.facematic.core.producer.builders;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Composite;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class CompositeBuilder extends ComponentBuilder {

	enum Locations {
		RESOURCE, FILE, URL
	}

	@Override
	public Class getBuildingClass() {
		return Composite.class;
	}
	

	@Override
	public void build(FaceProducer builder, Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		
		Object inner = prepareInnerComponent(builder, configuration);
		
		if (inner != null)
		if (inner instanceof AbstractComponent) {
			VerticalLayout panel = (VerticalLayout) oComponent;
			panel.addComponent((AbstractComponent) inner);
		}
	}

	

	
	protected Object prepareInnerComponent(FaceProducer builder, Element firstNested) {
		String sLocation = firstNested.attributeValue("location");
		String path = firstNested.attributeValue("path");
		String name = firstNested.attributeValue("name");
		
		String controllerClassName = firstNested.attributeValue("controller");
		Object controller = null;
		
		if (controllerClassName != null && !"".equals(controllerClassName)) {
			try {
				controller = builder.createClassInstance(controllerClassName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (sLocation == null)
			sLocation = "RESOURCE";

		Locations location = null;
		Object result = null;
		try {
			location = Locations.valueOf(sLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (location) {
		case RESOURCE:
			FaceProducer nestedBuilder = new FaceProducer(controller, builder, name);
			try {
				result = nestedBuilder.buildFromResource(path);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			break;
		case FILE:
		case URL:
			throw new RuntimeException("Not implemented yet");
		}

		return result;

	}

}
