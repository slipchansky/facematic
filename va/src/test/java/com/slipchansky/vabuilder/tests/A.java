package com.slipchansky.vabuilder.tests;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class A {
	
		public static void main(String[] args) throws Exception {

	        /*
	            Assume your application has a "home" directory
	            with /classes and /lib beneath it, where you can put
	            loose files and jars.

	            Thus,

	            /usr/local/src/APP
	            /usr/local/src/APP/classes
	            /usr/local/src/APP/lib
	         */

	        String HOME = "Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests";
	        String CLASSES = HOME+ "/classes";
	        //String LIB = HOME + "/lib";

	        // add the classes dir and each jar in lib to a List of URLs.
	        List<URL> urls = new ArrayList();
	        //urls.add(new File(CLASSES).toURL())
	        urls.add(new File("Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests/classes").toURL());
	        
//	        urls.add(new File("Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests/B.class").toURL());
//			urls.add(new File("Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests/C.class").toURL());
//	        urls.add(new File("Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests/A.class").toURL());
//	        urls.add(new File("Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests/B.class").toURL());
//	        urls.add(new File("Z:/workspace/facematic/va/src/test/java/com/slipchansky/vabuilder/tests/C.class").toURL());
	        
	        ;
	        /*
	        for (File f : new File(LIB).listFiles()) {
	            urls.add(f.toURL());
	        }
	        */

	        URL [] a = new URL[urls.size ()];
	        urls.toArray(a);
	        // feed your URLs to a URLClassLoader!
	        MyClassLoader classloader = new MyClassLoader(a, ClassLoader.getSystemClassLoader().getParent());
//	                new URLClassLoader(
//	                        urls.toArray(),
//	                        ClassLoader.getSystemClassLoader().getParent());

	        Class mainClass = classloader.loadClass("com.slipchansky.vabuilder.tests.B");
	        Method init = mainClass.getMethod("init", new Class[]{});

	        // well-behaved Java packages work relative to the
	        // context classloader.  Others don't (like commons-logging)
	        Thread.currentThread().setContextClassLoader(classloader);
	        init.invoke(null, null);

	    }		
	}

