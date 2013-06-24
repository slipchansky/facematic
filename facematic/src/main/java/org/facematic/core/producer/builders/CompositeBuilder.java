package org.facematic.core.producer.builders;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Composite;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class CompositeBuilder extends ComponentBuilder {
	private final static Logger logger = LoggerFactory.getLogger(CompositeBuilder.class);
	/**
	 * @author "Stanislav Lipchansky"
	 *
	 */
	enum Locations {
		RESOURCE, FILE, URL
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return Composite.class;
	}
	

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer producer, Object oComponent, Element configuration) {
		super.build(producer, oComponent, configuration);
		
		
		List<Element> substElements = (List<Element>)configuration.elements ("subst");
		
		Object inner = prepareInnerComponent(producer, configuration, substElements);
		
		if (inner != null)
		if (inner instanceof AbstractComponent) {
			VerticalLayout panel = (VerticalLayout) oComponent;
			panel.addComponent((AbstractComponent) inner);
		}
	}

	

	
	/**
	 * @param builder
	 * @param firstNested
	 * @param substElements 
	 * @return
	 */
	protected Object prepareInnerComponent(FaceProducer builder, Element firstNested, List<Element> substElements) {
		String sLocation = firstNested.attributeValue("location");
		String path = firstNested.attributeValue("path");
		String name = firstNested.attributeValue("name");
		String src = firstNested.attributeValue("src");
		if (src != null && !src.trim().equals("") ) {
			sLocation = "RESOURCE";
			path = src;
		}
		
		
		String controllerClassName = firstNested.attributeValue("controller");
		Object controller = null;
		
		if ("none".equals (controllerClassName) || "owner".equals (controllerClassName)) {
			controller = builder.getControllerInstance();
		}
		
		if (controller == null)
		if (controllerClassName != null && !"".equals(controllerClassName)) {
			try {
				controller = builder.createClassInstance(controllerClassName);
			} catch (Exception e) {
				logger.error("Could not create controllerInstance of "+controllerClassName, e);
			}
		}

		if (sLocation == null)
			sLocation = "RESOURCE";

		Locations location = null;
		Object result = null;
		try {
			location = Locations.valueOf(sLocation);
		} catch (Exception e) {
			logger.error("Invalid markup location:"+sLocation);
		}

		switch (location) {
		case RESOURCE:
			FaceProducer nestedBuilder = new FaceProducer(controller, builder, name);
			logger.error("------------------------------------------------------**************************-----------------------------");
			if (substElements != null) {
				for (Element e : substElements) {
					nestedBuilder.addSubst(e.attributeValue("name"), e.attributeValue("value"));
					logger.error("add subst : "+e.attributeValue("name")+"="+e.attributeValue("value"));
				}
			}
			try {
				result = nestedBuilder.buildFromResource(path);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			break;
		case FILE:
		case URL:
			logger.error("URL, FILE resource lcation not implemented yet");
			throw new RuntimeException("Not implemented yet");
		}

		return result;

	}

}
