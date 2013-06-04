package org.facematic.core.producer.builders;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class PanelBuilder extends ComponentBuilder {
	private final static Logger logger = LoggerFactory.getLogger(PanelBuilder.class);
	
	ComponentContainerBuilder innerBuilder = new ComponentContainerBuilder ();

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return Panel.class;
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object oComponent, Element configuration) {

		super.build(builder, oComponent, configuration);

		Object inner = prepareInnerComponent(builder, configuration);
		
		if (inner != null)
		if (inner instanceof AbstractComponent) {
			Panel panel = (Panel) oComponent;
			panel.setContent((AbstractComponent) inner);
		}
		
	}

	/**
	 * @param builder
	 * @param configuration
	 * @return
	 */
	protected Object prepareInnerComponent(FaceProducer builder, Element configuration) {
		
		VerticalLayout inner = new VerticalLayout ();
		innerBuilder.build(builder, inner, configuration);
		inner.setCaption(null);
		inner.setSizeFull();
		return inner;
		
		
//		Object inner = null;
//		List<Element> elements = configuration.elements();
//
//		if (elements.size() == 0)
//			return null;
//		
//		Element firstNested = elements.get(0);
//		
//		try {
//			inner = builder.build(firstNested);
//		} catch (Exception e) {
//			logger.error("Could not prepare inner component");
//		}
//		return inner;
	}
}
