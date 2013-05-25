package org.facematic.util.data.managedcontainer;

import org.facematic.util.data.managedcontainer.ManagedContainer;
import org.facematic.util.data.managedcontainer.RowControls;


public class ManagedContainerItem<TYPE> {
	private TYPE        bean;
	private RowControls controls;
	
	
	public ManagedContainerItem (TYPE bean, ManagedContainer container) {
		this.bean = bean;
		this.controls = new RowControls(this, container);
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
}