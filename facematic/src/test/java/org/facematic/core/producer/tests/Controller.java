package org.facematic.core.producer.tests;

import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.annotations.FmController;
import com.vaadin.ui.Component;

public class Controller implements FmBaseController {
	
	@FmController(name="first")
	ControllerA controllerA;
	
	@FmController(name="second")
	ControllerB controllerB;
	
	@FmViewComponent(name="view")
	Component view;
	
	@FmViewComponent(name="first")
	Component compositeFirst;
	
	@FmViewComponent(name="second")
	Component compositeSecond;
	
	@FmViewComponent(name="first.nestedA")
	Component firstNestedA;
	
	@FmViewComponent(name="second.nestedB")
	Component firstNestedB;
	
	@FmViewComponent(name="third.nestedB")
	Component thirdNestedB;

	public ControllerA getControllerA() {
		return controllerA;
	}

	public ControllerB getControllerB() {
		return controllerB;
	}

	public Component getView() {
		return view;
	}

	public Component getCompositeFirst() {
		return compositeFirst;
	}

	public Component getCompositeSecond() {
		return compositeSecond;
	}

	public Component getFirstNestedA() {
		return firstNestedA;
	}

	public Component getFirstNestedB() {
		return firstNestedB;
	}

	public Component getThirdNestedB() {
		return thirdNestedB;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		int k = 0;
		k++;
		
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.mvc.FmBaseController#prepareContext(org.facematic.core.producer.FaceProducer)
	 */
	@Override
	public void prepareContext(FaceProducer producer) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
