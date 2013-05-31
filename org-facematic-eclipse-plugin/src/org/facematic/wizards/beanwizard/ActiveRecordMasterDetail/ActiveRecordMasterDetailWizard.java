package org.facematic.wizards.beanwizard.ActiveRecordMasterDetail;

import org.facematic.wizards.beanwizard.BeanBrowseEdit.BeanBrowseEditBuilder;
import org.facematic.wizards.beanwizard.BeanBrowseEdit.FacematicNewBeanBrowseEditWizard;
import org.facematic.wizards.beanwizard.base.BeanFormWizardPage;

public class ActiveRecordMasterDetailWizard extends FacematicNewBeanBrowseEditWizard {
	
	private ActiveRecordMasterDetailWizardPage activeRecordMasterDetailWizardPage;
	private ActiveRecordMasterDetailBuilder activeRecordMasterDetailWizardBuilder;
	

	public BeanBrowseEditBuilder getBuilder() {
		return builder = activeRecordMasterDetailWizardBuilder = new ActiveRecordMasterDetailBuilder();
	}

	@Override
	protected BeanFormWizardPage newBeanFormWizardPage() {
		return page = activeRecordMasterDetailWizardPage = new ActiveRecordMasterDetailWizardPage (selection);
	}
	
	@Override
	protected void prepareFinishContext() {
		super.prepareFinishContext();
		
		activeRecordMasterDetailWizardBuilder.setEditorName (activeRecordMasterDetailWizardPage.getEditorNameText());
		activeRecordMasterDetailWizardBuilder.setDaoName (activeRecordMasterDetailWizardPage.getDaoName ());
		activeRecordMasterDetailWizardBuilder.setDaoFakeName (activeRecordMasterDetailWizardPage.getDaoFakeName ());
	}
	
	
	

}
