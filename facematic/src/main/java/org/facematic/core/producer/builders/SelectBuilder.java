package org.facematic.core.producer.builders;

import org.dom4j.Element;

import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.AbstractSelect;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class SelectBuilder extends AbstractFieldBuilder {

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
