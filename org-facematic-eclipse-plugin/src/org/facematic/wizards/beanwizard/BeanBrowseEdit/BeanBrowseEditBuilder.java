package org.facematic.wizards.beanwizard.BeanBrowseEdit;

import java.lang.reflect.Method;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.facematic.wizards.beanwizard.base.BeanFormBuilder;

public class BeanBrowseEditBuilder extends BeanFormBuilder {

	

	protected String editFormSimpleName;
	protected String editFormQualifiedName;
	protected String editViewQualifiedName;
	protected String browserViewSimpleName;
	protected String browserViewQualifiedName;
	protected String createFormSimpleName;
	protected String createFormQualifiedName;
	protected String createViewQualifiedName;
	protected String editViewSimpleName;
	protected String creatorFormSimpleName;

	@Override
	protected void prepareEnvironment() {
		super.prepareEnvironment();
		
		
		editFormQualifiedName = this.controllerPackageName+"."+editFormSimpleName;
		editViewQualifiedName = this.viewPackageName+"."+editFormSimpleName;
		editViewSimpleName = editFormSimpleName;
		
		browserViewSimpleName = this.controllerSimpleName;
		browserViewQualifiedName = this.controllerQualifiedName;
		createFormSimpleName = creatorFormSimpleName;
		createFormQualifiedName = this.controllerPackageName+"."+createFormSimpleName;
		createViewQualifiedName = this.viewPackageName+"."+createFormSimpleName;
		put ("editFormSimpleName", editFormSimpleName);
		put ("editFormQualifiedName", editFormQualifiedName);
		                         
		put ("editViewQualifiedName", editViewQualifiedName);
		put ("browserViewSimpleName", browserViewSimpleName);
		put ("browserViewQualifiedName", browserViewQualifiedName);
		put ("createFormSimpleName", createFormSimpleName);
		put ("createFormQualifiedName", createFormQualifiedName);
		put ("createViewQualifiedName", createViewQualifiedName);
		
	}

	@Override
	protected IFile beanClassProcess() {
		
		
		
		Method [] methods = beanClass.getMethods();
		
		String editorContollerFields = ""; 
		
		String browserViewColumns = "";
		
		String editorViewFields = "";
		
		for (Method m : methods) {
			String fieldName = m.getName();
			if (!fieldName.startsWith("set")) {
				continue;
			}
			fieldName = fieldName.substring(3, fieldName.length());
			fieldName = fieldName.substring(0, 1).toLowerCase()+fieldName.substring(1, fieldName.length());
			
			translator.put("fieldName", fieldName);
			translator.put("fieldCaption", fieldName);
			
			editorContollerFields+=translator.evaluateTemplate("BeanBrowseEdit/controller/formFieldItem.vm");
			
			browserViewColumns+=translator.evaluateTemplate("BeanBrowseEdit/view/tableColumnItem.vm");
			editorViewFields+=translator.evaluateTemplate("BeanBrowseEdit/view/formFieldItem.vm");
		}
		
		translator.put ("editorContollerFields", editorContollerFields);
		translator.put ("browserViewColumns", browserViewColumns);
		translator.put ("editorViewFields", editorViewFields);
		
		
		String browserControllerCode = translator.evaluateTemplate("BeanBrowseEdit/controller/controller.vm");
		String browserViewCode = translator.evaluateTemplate("BeanBrowseEdit/view/TheBeanBrowser.vm"); 
		
		String editorControllerCode = translator.evaluateTemplate("BeanBrowseEdit/controller/FormEdit.vm"); 
		String editorViewCode = translator.evaluateTemplate("BeanBrowseEdit/view/TheBeanForm.vm"); //
		
		String creatorControllerCode = translator.evaluateTemplate("BeanBrowseEdit/controller/FormCreate.vm");
		
		 
		IFile file = 
		writeJavaCode (controllerSimpleName,  browserControllerCode);
		writeXml      (browserViewSimpleName, browserViewCode);
		
		writeJavaCode (editFormSimpleName, editorControllerCode);
		writeXml(editViewSimpleName, editorViewCode);
		
		writeJavaCode(createFormSimpleName, creatorControllerCode);
		
		
		return file;
		
		
		
	}

	protected File writeXml(String fileName, String xmlCode) {
		return writeSourceFile("src.main.resources."+viewPackageName, fileName+".xml", xmlCode);
	}

	protected File writeJavaCode(String fileName, String javaCode) {
		return writeSourceFile("src.main.java."+controllerPackageName, fileName+".java", javaCode);
	}
	
	protected File writeTestJavaCode(String fileName, String javaCode) {
		return writeSourceFile("src.test.java."+controllerPackageName, fileName+".java", javaCode);
	}
	

	public void setEditorName(String editorNameText) {
		this.editFormSimpleName = editorNameText;
		
	}

	public void setCreatorName(String creatorNameText) {
		this.creatorFormSimpleName = creatorNameText;
	}


}
