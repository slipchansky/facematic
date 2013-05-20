package org.facematic.wizards.beanwizard.base;

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
import org.facematic.Activator;
import org.facematic.Settings;
import org.facematic.plugin.utils.FmProjectSupport;
import org.facematic.plugin.utils.SimpleTemplateEngine;
//import org.facematic.plugin.utils.TestBean;

/**
 * @author "Stanislav Lipchansky"
 *
 */
@SuppressWarnings("restriction")
public class BeanFormBuilder {
	private IProject classProject ;
	private URLClassLoader projectClassLoader;
	
	protected Class<?>  beanClass; // = org.facematic.plugin.utils.TestBean.class;
	private String beanClassSimpleName;
	private String beanClassQualifiedName; // = org.facematic.plugin.utils.TestBean.class.getCanonicalName();
	private String beanFormControllerName      = "EditTestBeanFormController";
	private String destinationPackagePath = "/org.facematic.eclipse.plugin/src/org/facematic/plugin/utils/views";
	
	protected String viewSimpleName;
	protected String viewPackageName;
	protected String viewQualifiedName;
	
	protected String controllerSimpleName;
	protected String controllerPackageName;
	protected String controllerQualifiedName;
	
	protected String formSimpleName;
	
	protected FmProjectSupport projectSupport;
	protected IProject targetProject;
	
	
	String formFields = "", viewFields = "";
	
	protected SimpleTemplateEngine translator = new SimpleTemplateEngine ();
	private String controllerCode;
	private String viewCode;
	
	
	public void setClassProject(IProject classProject) {
		this.classProject = classProject;
		 
	}

	public void setBeanClassQualifiedName(String beanClassQualifiedName) {
		this.beanClassQualifiedName = beanClassQualifiedName;
		
	}

	public void setBeanFormControllerName(String beanFormControllerName) {
		this.beanFormControllerName = beanFormControllerName;
	}

	public void setDestinationPackagePath(String destinationPackagePath) {
		this.destinationPackagePath = destinationPackagePath;
	}

	public void setTargetProject(IProject targetProject) {
		this.targetProject = targetProject;
	}

	public BeanFormBuilder() {
	}

	/**
	 * @return
	 */
	public final IFile process () {
		prepareEnvironment ();
		return beanClassProcess ();
	}
	

	protected void prepareEnvironment() {
		
		try {
			prepareProjectClassLoader ();
		} catch (Exception e) {
		}
		try {
			beanClass = projectClassLoader.loadClass(beanClassQualifiedName);
		} catch (ClassNotFoundException e) {
		}
		projectSupport = new FmProjectSupport(targetProject);
		
		String a[];
		a = destinationPackagePath.split("/src/main/java/");
		if (a.length < 2) 
			a = destinationPackagePath.split("/src/main/resources/");
		if (a.length < 2)
			a = destinationPackagePath.split("/src/");
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
		
		
		viewSimpleName = beanFormControllerName;
		controllerSimpleName = beanFormControllerName;
		if (viewSimpleName.endsWith("Controller")) {
			viewSimpleName = viewSimpleName.substring(0, viewSimpleName.length ()-"Controller".length())+"View";
		}
		if (controllerSimpleName.endsWith("View")) {
			controllerSimpleName = controllerSimpleName.substring(0, controllerSimpleName.length ()-"View".length())+"Controller";
		}
		
		a = beanClassQualifiedName.split("\\.");
		beanClassSimpleName = a[a.length-1]; 
		
		viewQualifiedName = viewPackageName+"."+viewSimpleName;
		controllerQualifiedName = controllerPackageName+"."+controllerSimpleName;
		
		formSimpleName = beanFormControllerName.substring(0, 1).toLowerCase()+beanFormControllerName.substring(1, beanFormControllerName.length());
		
		
		translator.put("classSimpleName", beanClassSimpleName);
		translator.put("className", beanClassQualifiedName); 
		translator.put("formName", beanFormControllerName); 
		translator.put("containerName", destinationPackagePath); 
		translator.put("controllerPackageName", controllerPackageName); 
		translator.put("viewPackageName", viewPackageName); 
		translator.put("viewQualifiedName", viewQualifiedName); 
		translator.put("controllerSimpleName", controllerSimpleName); 
		translator.put("viewSimpleName", viewSimpleName); 
		translator.put("controllerQualifiedName", controllerQualifiedName); 
		translator.put("formSimpleName", formSimpleName); 
		translator.put("INDENT", Settings.INDENT); 
	}
	
	/**
	 * @return
	 */
	protected IFile beanClassProcess() {
		Method [] methods = beanClass.getMethods();
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
		
		IFile file = writeSourceFile("src.main.java."+controllerPackageName, controllerSimpleName+".java", controllerCode);
		writeSourceFile("src.main.resources."+viewPackageName, viewSimpleName+".xml", viewCode);
		
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
	
	

	/**
	 * @param packageName
	 * @param fileName
	 * @param code
	 * @return
	 */
	protected File writeSourceFile(String packageName, String fileName, String code) {
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
	
	public void put (String key, String value) {
		translator.put(key, value);
	}
	

}
