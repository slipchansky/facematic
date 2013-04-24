package org.facematic.core.producer.builders;

import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import org.facematic.utils.GroovyEngine;
import com.vaadin.ui.Button;

public class ButtonBuilder extends ComponentBuilder {
	@Override
	public Class getBuildingClass() {
		return Button.class;
	}

	@Override
	public void build(FaceProducer builder, Object viewInstance, Element configuration) {
		super.build(builder, viewInstance, configuration);
		
		Element onClickNode = configuration.element("onClick");
		
// TODO Переделать(или расширить) с мепингом события на метод контроллера
		
		if (onClickNode != null) {
			final String onClickScript = onClickNode.getText();
			
			final GroovyEngine engine = builder.getGroovyEngine();
			Button button = (Button) viewInstance;
			button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					engine.evaluate(onClickScript);
				}
			});
		}

	}

}
