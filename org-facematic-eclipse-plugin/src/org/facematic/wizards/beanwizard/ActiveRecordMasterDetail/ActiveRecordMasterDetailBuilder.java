package org.facematic.wizards.beanwizard.ActiveRecordMasterDetail;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.facematic.wizards.beanwizard.BeanBrowseEdit.BeanBrowseEditBuilder;

public class ActiveRecordMasterDetailBuilder extends BeanBrowseEditBuilder {
	
	private String fakeDaoName;
	private String daoName;

	@Override
	protected IFile beanClassProcess() {
		
		
		
		Method [] methods = beanClass.getMethods();
		
		String editorContollerFields = ""; 
		
		String browserViewColumns = "";
		
		String editorViewFields = "";
		translator.put ("daoName", daoName);
		translator.put ("fakeDaoName", fakeDaoName);
		
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
		
		
		String browserControllerCode = translator.evaluateTemplate("ActiveRecordMD/browse/BeanBrowse.jav");
		String browserViewCode = translator.evaluateTemplate("ActiveRecordMD/browse/BeanBrowse.xml"); 
		
		String editorControllerCode = translator.evaluateTemplate("ActiveRecordMD/edit/BeanEdit.jav"); 
		String editorViewCode = translator.evaluateTemplate("ActiveRecordMD/edit/BeanEdit.xml"); //
		
		String daoCode = translator.evaluateTemplate("ActiveRecordMD/dao/BeanDao.jav");
		String daoFakeCode = translator.evaluateTemplate("ActiveRecordMD/dao/BeanDaoFake.jav");
		
		 
		IFile file = 
		writeJavaCode (controllerSimpleName,  browserControllerCode);
		writeXml      (browserViewSimpleName, browserViewCode);
		
		writeJavaCode (editFormSimpleName, editorControllerCode);
		writeXml(editViewSimpleName, editorViewCode);
		
		writeJavaCode(daoName, daoCode);
		writeTestJavaCode(fakeDaoName, daoFakeCode);
		
		
		return file;
		
		
		
	}

	public void setDaoName(String daoName) {
		this.daoName = daoName;
		
	}

	public void setDaoFakeName(String daoFakeName) {
		this.fakeDaoName = daoFakeName;
		
	}
	

}
