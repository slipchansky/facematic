package org.facematic.util.data.managedcontainer;

import org.facematic.util.data.managedcontainer.ManagedContainer;
import org.facematic.util.data.managedcontainer.RowControls;


public class ManagedContainerItem<TYPE> {
	private TYPE        bean;
	private RowControls controls;
	private boolean isNewInstance;
	
	
	public ManagedContainerItem (TYPE bean, ManagedContainer container) {
		this.bean = bean;
		this.controls = new RowControls(this, container);
		if (bean==null) {
			controls.hideAction(Action.DELETE);
			controls.hideAction(Action.UP);
			controls.hideAction(Action.DOWN);
		}
	}
	
	public TYPE getBean() {
		return bean;
	}
	public void setBean(TYPE bean) {
		this.bean = bean;
	}
	
	public RowControls getControls() {
		return controls;
	}
	public void setControls(RowControls controls) {
		this.controls = controls;
	}

	public void setIsNewInstance(boolean b) {
		this.isNewInstance = b;
	}
	public boolean isNewInstance () {
		return isNewInstance;
	}
	
}