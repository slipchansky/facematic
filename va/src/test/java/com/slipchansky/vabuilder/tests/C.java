package com.slipchansky.vabuilder.tests;

public class C {

	public void start() {
		Class classLoader = this.getClass ().getClassLoader ().getClass();
		System.out.println ("classLoader: "+classLoader.getSimpleName());
		
		System.out.println ("C is started");
		
	}

}
