package org.facematic.wizards.beanwizard.ActiveRecordMasterDetail;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.facematic.wizards.beanwizard.BeanBrowseEdit.BeanBrowseEditWizardPage;

public class ActiveRecordMasterDetailWizardPage extends BeanBrowseEditWizardPage {

	Text daoName;
	Text fakeDaoName;
	
	public ActiveRecordMasterDetailWizardPage(ISelection selection) {
		super(selection);
	}
	
	@Override
	protected void addMoreControls (Composite container) {
		editorNameText = addInputLine(container, "&Details Controller Name:");
		daoName = addInputLine(container, "Da&o Class Name:");
		fakeDaoName = addInputLine(container, "&Fake Dao Class Name:");
	}

	@Override
	protected void updateInputsForBeanClass(IType type) {
		controllerNameText.setText(type.getElementName()+"Browser");
		editorNameText.setText(type.getElementName()+"FormEdit");
		daoName.setText(type.getElementName()+"Dao");
		fakeDaoName.setText(type.getElementName()+"DaoFake");
	}

	public String getDaoName() {
		return daoName.getText();
	}
	
	public String getDaoFakeName() {
		return fakeDaoName.getText();
	}

}
