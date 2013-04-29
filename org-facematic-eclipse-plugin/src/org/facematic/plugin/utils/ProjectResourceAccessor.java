package org.facematic.plugin.utils;

import java.io.InputStream;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.facematic.utils.ResourceAccessor;

public class ProjectResourceAccessor implements ResourceAccessor {
	
	private IContainer project;

	public ProjectResourceAccessor (IContainer project) {
		this.project = project;
	}

	@Override
	public InputStream getResourceStream(String path) {
		
		if (path.indexOf('/') < 0)
		if (path.endsWith(".java")) {
			path = path.substring(0, path.length()-5);
			path = path.replace('.', '/')+".java";
		} else if (path.endsWith(".xml")) {
			path = path.substring(0, path.length()-4);
			path = path.replace('.', '/')+".xml";
		} else if (path.endsWith(".class")) {
			path = path.substring(0, path.length()-6);
			path = path.replace('.', '/')+".class";
		}
		
		
		InputStream is = getResourceStream ("src", path);
		if (is==null) {
			is = getResourceStream ("src/resources", path);
		}
		if (is==null) {
			is = getResourceStream ("src/java", path);
		}
		if (is==null) {
			is = getResourceStream ("target/classes", path);
		}
		if (is==null) {
			is = getResourceStream ("bin", path);
		}
		
		
		return is;
		
	}

	private InputStream getResourceStream(String prefix, String path) {
		 path = prefix +'/'+path;
		 Path   resourcePath = new Path (path);
		 File file = (File) project.getFile(resourcePath);
		 
		 if (!file.exists()) return null;
		 try {
			return file.getContents();
		} catch (CoreException e) {
			return null;
		}
	}

}
