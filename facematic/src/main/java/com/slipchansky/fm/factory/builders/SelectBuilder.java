package com.slipchansky.fm.factory.builders;

import org.dom4j.Element;

import com.slipchansky.fm.producer.FaceProducer;
import com.vaadin.ui.AbstractSelect;

public class SelectBuilder extends ComponentBuilder {

	@Override
	public Class getBuildingClass() {
		return AbstractSelect.class;
	}

	@Override
	public void build(FaceProducer builder, Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
	}
	
	

}
