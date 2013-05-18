package org.facematic.plugin.utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.Servlet;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.facematic.Activator;
import org.facematic.Settings;

public class BeanFormBuilder {
	private IProject classProject ;
	private Class clazz = org.facematic.plugin.utils.TestBean.class;
	private URLClassLoader projectClassLoader;
	
	private String className = org.facematic.plugin.utils.TestBean.class.getCanonicalName();
	private String formName      = "EditTestBeanFormController";
	private String containerName = "/org.facematic.eclipse.plugin/src/org/facematic/plugin/utils/views";
	
	private String controllerPackageName;
	private String viewPackageName;
	private String viewQualifiedName;
	private String controllerSimpleName;
	private String viewSimpleName;
	private String controllerQualifiedName;
	private String formSimpleName;
	String formFields = "", viewFields = "";
	
	SimpleTemplateEngine translator = new SimpleTemplateEngine ();
	private String controllerCode;
	private String viewCode;
	private String classSimpleName;
	private FmProjectSupport projectSupport;
	private IProject targetProject;
	
	public BeanFormBuilder(String className, String formName, String containerName, IProject classProject, IProject targetProject) throws Exception {
		super();
		this.className = className;
		this.formName = formName;
		this.containerName = containerName;
		this.classProject = classProject;
		this.targetProject = targetProject;
		prepareProjectClassLoader ();
		clazz = projectClassLoader.loadClass(className);
		projectSupport = new FmProjectSupport(targetProject);
	}
	
	public BeanFormBuilder() {
		// TODO Auto-generated constructor stub
	}

	public IFile process () {
		prepareEnvironment ();
		return beanClassProcess ();
	}
	

	private void prepareEnvironment() {
		String a[];
		a = containerName.split("/src/main/java/");
		if (a.length < 2) 
			a = containerName.split("/src/main/resources/");
		if (a.length < 2)
			a = containerName.split("/src/");
		if (a.length < 2)
			return;
		
		String base = a[1].replace('/', '.');
		if (!base.endsWith("."))
			base += ".";
		
		controllerPackageName = base;
		viewPackageName = base;
		
		if (base.indexOf (".controllers.") > 0 ) {
			controllerPackageName = base;
			viewPackageName = base.replace(".controllers.", ".views.");
		} else if (base.indexOf (".views.") > 0 ) {
			controllerPackageName = base.replace(".views.", ".controllers.");
			viewPackageName = base;
		}
		controllerPackageName = controllerPackageName.substring(0, controllerPackageName.length()-1);
		viewPackageName = viewPackageName.substring(0, viewPackageName.length()-1);
		
		
		viewSimpleName = formName;
		controllerSimpleName = formName;
		if (viewSimpleName.endsWith("Controller")) {
			viewSimpleName = viewSimpleName.substring(0, viewSimpleName.length ()-"Controller".length())+"View";
		}
		if (controllerSimpleName.endsWith("View")) {
			controllerSimpleName = controllerSimpleName.substring(0, controllerSimpleName.length ()-"View".length())+"Controller";
		}
		
		a = className.split("\\.");
		classSimpleName = a[a.length-1]; 
		
		viewQualifiedName = viewPackageName+"."+viewSimpleName;
		controllerQualifiedName = controllerPackageName+"."+controllerSimpleName;
		
		formSimpleName = formName.substring(0, 1).toLowerCase()+formName.substring(1, formName.length());
		
		
		translator.put("classSimpleName", classSimpleName);
		translator.put("className", className); 
		translator.put("formName", formName); 
		translator.put("containerName", containerName); 
		translator.put("controllerPackageName", controllerPackageName); 
		translator.put("viewPackageName", viewPackageName); 
		translator.put("viewQualifiedName", viewQualifiedName); 
		translator.put("controllerSimpleName", controllerSimpleName); 
		translator.put("viewSimpleName", viewSimpleName); 
		translator.put("controllerQualifiedName", controllerQualifiedName); 
		translator.put("formSimpleName", formSimpleName); 
		translator.put("INDENT", Settings.INDENT); 
	}
	
	private IFile beanClassProcess() {
		Method [] methods = clazz.getMethods();
		formFields = ""; viewFields = "";
		for (Method m : methods) {
			String fieldName = m.getName();
			if (!fieldName.startsWith("set")) {
				continue;
			}
			fieldName = fieldName.substring(3, fieldName.length());
			fieldName = fieldName.substring(0, 1).toLowerCase()+fieldName.substring(1, fieldName.length());
			translator.put("fieldName", fieldName);
			translator.put("fieldCaption", fieldName);
			
			formFields+=translator.evaluateTemplate("BeanFormBuilder/controller/fieldItem.vm");
			viewFields+=translator.evaluateTemplate("BeanFormBuilder/view/fieldItem.vm");
		}
		
		translator.put ("formFields", formFields);
		translator.put ("viewFields", viewFields);
		viewCode = translator.evaluateTemplate("BeanFormBuilder/view/view.vm");
		controllerCode = translator.evaluateTemplate("BeanFormBuilder/controller/controller.vm");
		
		IFile file = updateSourceFile("src.main.java."+controllerPackageName, controllerSimpleName+".java", controllerCode);
		updateSourceFile("src.main.resources."+viewPackageName, viewSimpleName+".xml", viewCode);
		
		return file;
		
		
	}
	

	private void prepareProjectClassLoader () throws Exception {
		IJavaProject javaProject = JavaCore.create(classProject);
		String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
		
		Set<URL> urlList = new LinkedHashSet<URL>();
		for (int i = 0; i < classPathEntries.length; i++) {
			 String entry = classPathEntries[i];
			 IPath path = new Path(entry);
			 URL url = path.toFile().toURI().toURL();
			 urlList.add(url);
		}
		
		URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
		projectClassLoader = new URLClassLoader(urls, Servlet.class.getClassLoader());
	}
	
	

	private File updateSourceFile(String packageName, String fileName, String code) {
		String packagePath = packageName;
		packagePath = packagePath.replace('.', '/');
		
		try {
			projectSupport.createProjectFolder(packagePath);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		String sourceFileName = packagePath+"/"+fileName;
		

		Path javaPath = new Path(sourceFileName);
		File file = (File) targetProject.getFile(javaPath);
		if (!file.exists()) {
			try {
				file.create(new ByteArrayInputStream(code.getBytes()), true, null);
				return file;
			} catch (CoreException e) {
				Activator.error ("Can't create file "+sourceFileName, e);
				return null;
			}
		}
		return file;

	}
	

}
