package org.facematic.wizards.beanwizard.BeanBrowseEdit;

import org.eclipse.core.resources.IFile;
import org.facematic.wizards.beanwizard.base.BeanBaseWizard;
import org.facematic.wizards.beanwizard.base.BeanFormBuilder;
import org.facematic.wizards.beanwizard.base.BeanFormWizardPage;

public class FacematicNewBeanBrowseEditWizard extends BeanBaseWizard {
	
	
	protected BeanBrowseEditWizardPage page;
	protected BeanBrowseEditBuilder builder;

	protected BeanFormBuilder getBuilderInstance () {
		return builder;
	}

	@Override
	protected BeanFormWizardPage newBeanFormWizardPage() {
		return page = new BeanBrowseEditWizardPage (selection);
	}

	@Override
	protected void prepareFinishContext() {
		super.prepareFinishContext();
		builder = getBuilder();
		builder.setEditorName (page.getEditorNameText());
		builder.setCreatorName(page.getCreatorNameText ());
	}

	public BeanBrowseEditBuilder getBuilder() {
		return new BeanBrowseEditBuilder();
	}
	
	
	
	

}
