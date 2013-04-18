package com.slipchansky.fm.factory.builders;

import org.dom4j.Element;

import com.slipchansky.fm.factory.FaceFactory;
import com.slipchansky.utils.GroovyEngine;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ButtonBuilder extends ComponentBuilder {

	@Override
	public Class getBuildingClass() {
		return Button.class;
	}

	@Override
	public void build(final FaceFactory builder, Object oComponent,
			Element configuration) {
		super.build(builder, oComponent, configuration);
		
		Element onClickNode = configuration.element("onClick");
		if (onClickNode != null) {
			final String onClickScript = onClickNode.getText();
			
			final GroovyEngine engine = builder.getEngine();
			Button button = (Button) oComponent;
			button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					engine.evaluate(onClickScript);
				}
			});
		}

	}

}
