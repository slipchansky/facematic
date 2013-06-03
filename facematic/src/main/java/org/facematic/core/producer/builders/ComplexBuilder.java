package org.facematic.core.producer.builders;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Complex;

public class ComplexBuilder extends  ComponentContainerBuilder {
	private static Logger logger = LoggerFactory.getLogger(ComplexBuilder.class);

	@Override
	public Class getBuildingClass() {
		return Complex.class;
	}

	@Override
	public void build(FaceProducer builder, Object viewInstance, Element configuration) {
		Element contentNode = configuration.element("content");
		
		String controllerClassName = contentNode.attributeValue("controller");
		if (controllerClassName != null) {
			try {
				builder.createControllerInstance(controllerClassName);
			} catch (Exception e) {
				logger.error ("Cannot create controller instance of "+controllerClassName);
			}
		}
		
		for (Element e : (List<Element>)configuration.elements()) {
			if (!"content".equals(e.getName()) ) {
				String name = e.attributeValue("name");
				if (name != null)
					builder.addComplexResource (name, e.asXML ());
			}
		}
		
		super.build(builder, viewInstance, contentNode);
	}

}
