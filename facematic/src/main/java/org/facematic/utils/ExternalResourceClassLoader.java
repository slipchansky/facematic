package org.facematic.utils;

import java.io.IOException;
import java.io.InputStream;

public class ExternalResourceClassLoader extends ClassLoader {
	private ResourceAccessor resourceAccessor;

	public ExternalResourceClassLoader (ResourceAccessor accessor) {
		this.resourceAccessor = accessor;
	}

	@Override
	protected Class findClass(String name) throws ClassNotFoundException {
		
		try {
		   Class clazz = super.findClass(name);
		   return clazz;
		} catch (Exception e) {
		}
		
		
		byte[] ba;
		try {
			String classResourceName =  name.replace('.', '/')+".class";
			ba = getClassBytes (classResourceName);
		} catch (IOException e) {
			throw new RuntimeException (e);
		}
		return defineClass(name, ba, 0, ba.length);
	}

	private byte[] getClassBytes(String name) throws IOException {
		InputStream is = resourceAccessor.getResourceStream(name);
		if (is==null) return null;
		return StreamUtils.getBytes(is);
	}

}
