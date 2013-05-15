package org.facematic.core.producer.builders;


import java.lang.reflect.Method;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Field.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class ComponentBuilder extends BeanBuilder {
	private final static Logger logger = LoggerFactory.getLogger(ComponentBuilder.class);
	
	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.BeanBuilder#getBuildingClass()
	 */
	public Class getBuildingClass () {
		return AbstractComponent.class;
	}
	
	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.BeanBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	public void build (FaceProducer builder, Object viewInstance, Element configuration) {
		super.build (builder, viewInstance, configuration);
		
		Attribute sizeFullAttr = configuration.attribute("sizeFull");
		if (sizeFullAttr != null) {
			if ("true".equals(sizeFullAttr.getValue())) {
				((AbstractComponent)viewInstance).setSizeFull();
			} else {
				((AbstractComponent)viewInstance).setSizeUndefined();
			}
		}
		
	}
	
	/**
	 * Helps to get the methods (primarily event listener methods) in descendants  
	 * 
	 * @param producer
	 * @param configuration 
	 * @param methodName
	 * @param methodAttrClass
	 * @return
	 */
	protected Method  getMethod(Object source, FaceProducer producer, Element configuration, String attrName, Class methodAttrClass) {
		String methodName = configuration.attributeValue(attrName);
		if (methodName==null) return null;
		if (methodName.trim().equals("")) return null;
		Object controller = producer.getControllerInstance();
		if (controller==null) return null;
		String objectName    =  configuration.attributeValue("name");
		String objectCaption =  configuration.attributeValue("caption");
		producer.setListener(methodName, methodAttrClass, source.getClass(), objectName, objectCaption, attrName);
		
		
		try {
			return controller.getClass().getMethod(methodName, methodAttrClass);
		} catch (Exception e) {
			return null;
		}
	}	

}
