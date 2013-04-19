package com.slipchansky.vabuilder.tests;

public class B {
	public static void init () {
		C c = new C ();
		Class classLoader = Thread.currentThread().getClass ().getClassLoader ().getClass();
		System.out.println ("classLoader: "+classLoader.getSimpleName());
		
		c.start ();
	}

}
