package org.facematic.core.producer.builders;


import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;

import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class ComponentBuilder extends BeanBuilder {
	
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

}
