package org.facematic.core.producer.builders;

import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractSelect;

public class SelectBuilder extends AbstractFieldBuilder {

	@Override
	public Class getBuildingClass() {
		return AbstractSelect.class;
	}

	@Override
	public void build(FaceProducer builder, final Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		
	}
	
	

}
