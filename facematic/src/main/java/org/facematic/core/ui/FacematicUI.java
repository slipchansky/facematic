package org.facematic.core.ui;

import java.util.List;

import org.facematic.cdi.FmCdiEntryPoint;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.producer.FaceReflectionHelper;
import com.vaadin.ui.UI;

public abstract class FacematicUI extends UI {
	private List<FmCdiEntryPoint> injections;
	protected FaceProducer producer;
	
	
	public FacematicUI () {
		producer =new FaceProducer (this, this);
	}
	
	public List<FmCdiEntryPoint>  getInjections () {
		return injections;
	}
	
	public void setInjections (List<FmCdiEntryPoint> injections) {
		this.injections = injections;
		applyInjections  (injections);
	}

	private void applyInjections(List<FmCdiEntryPoint> injections) {
		FaceReflectionHelper reflectionHelpler = new FaceReflectionHelper (this);
		reflectionHelpler.addUiInjections (this);
	}
	

}
