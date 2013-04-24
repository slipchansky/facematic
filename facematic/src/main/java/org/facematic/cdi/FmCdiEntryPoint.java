package org.facematic.cdi;

public class FmCdiEntryPoint {
	Class entryPointClass;
	Object value;
	
	
	public FmCdiEntryPoint(Class entryPointClass, Object value) {
		super();
		this.entryPointClass = entryPointClass;
		this.value = value;
	}


	public Class getEntryPointClass() {
		return entryPointClass;
	}

	public Object getValue() {
		return value;
	}
	
	
	

}
