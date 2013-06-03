package org.facematic.core.producer.builders;

import java.util.List;

import org.dom4j.Element;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Complex;

public class ComplexBuilder extends  ComponentContainerBuilder {

	@Override
	public Class getBuildingClass() {
		return Complex.class;
	}

	@Override
	public void build(FaceProducer builder, Object viewInstance, Element configuration) {
		Element contentNode = configuration.element("content");
		
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
