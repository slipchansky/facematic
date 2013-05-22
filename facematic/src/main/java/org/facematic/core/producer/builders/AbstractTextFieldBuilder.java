package org.facematic.core.producer.builders;

import org.dom4j.Element;
import org.facematic.core.producer.FaceProducer;

import com.vaadin.ui.AbstractTextField;

public class AbstractTextFieldBuilder extends AbstractFieldBuilder {

	@Override
	public Class getBuildingClass() {
		return AbstractTextField.class;
	}

	@Override
	public void build(FaceProducer builder, Object oComponent,
			Element configuration) {
		AbstractTextField field = (AbstractTextField)oComponent;
		field.setNullRepresentation("");
		super.build(builder, oComponent, configuration);
	}
	

}
