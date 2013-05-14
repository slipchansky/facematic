package org.facematic.core.producer.builders;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractSelect;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class SelectBuilder extends AbstractFieldBuilder {
	private final static Logger logger = LoggerFactory.getLogger(SelectBuilder.class);

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.AbstractFieldBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return AbstractSelect.class;
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.AbstractFieldBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, final Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		
	}
	
	

}
