package org.facematic.core.producer.builders;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractComponent;
import org.dom4j.tree.DefaultAttribute;
import java.lang.reflect.Method;
import java.util.List;

public class BeanBuilder {
	
	private final static Logger logger = LoggerFactory.getLogger(BeanBuilder.class);
	

	public Class getBuildingClass () {
		return Object.class;
	}
	
	public void build (FaceProducer builder, Object viewInstance, Element configuration) {
		applyAttrinutes (viewInstance, configuration);
	}
	
	private void applyAttrinutes (Object instance, Element node) {
		List<DefaultAttribute> attributes = node.attributes();
		if (attributes == null) {
			return ;
		}
			
		for (DefaultAttribute attr : attributes) {
			String attrname = attr.getName();
			String value = attr.getValue();
			String name = "set"+attrname.substring(0, 1).toUpperCase()+attrname.substring(1);
			try {
			    Method setter = instance.getClass().getMethod(name, String.class);
			    setter.invoke(instance, value);
			} catch (Exception e) {
				try {
				Method setter = instance.getClass().getMethod(name, Boolean.TYPE);
				setter.invoke(instance, value.toLowerCase().equals("true"));
				} catch (Exception e1) {
					
					try {
						Method setter = instance.getClass().getMethod(name, Integer.TYPE);
						setter.invoke(instance, new Integer (value));
						} catch (NoSuchMethodException ensm) {
							// just skip. there
						} catch (Exception other) {
							logger.warn("Could not set "+instance.getClass().getCanonicalName()+"."+attrname+"="+value); 
						}
					
				}
			}
		}
	}
	

}
