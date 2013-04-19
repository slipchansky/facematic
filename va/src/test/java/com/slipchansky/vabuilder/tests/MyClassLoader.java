package com.slipchansky.vabuilder.tests;

import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader extends URLClassLoader {

	

	public MyClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	protected Class<?> loadClass(String arg0, boolean arg1) throws ClassNotFoundException {
		System.out.println("loadClass(String arg0, boolean arg1) for: "+arg0);
		return super.loadClass(arg0, arg1);
	}

	@Override
	public Class<?> loadClass(String arg0) throws ClassNotFoundException {
		System.out.println("loadClass(String arg0) for: "+arg0);
		return super.loadClass(arg0);
	}
	

}
