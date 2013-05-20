package org.facematic.wizards.beanwizard.BeanBrowseEdit;

import org.eclipse.core.resources.IFile;
import org.facematic.wizards.beanwizard.base.BeanBaseWizard;
import org.facematic.wizards.beanwizard.base.BeanFormBuilder;
import org.facematic.wizards.beanwizard.base.BeanFormWizardPage;

public class FacematicNewBeanBrowseEditWizard extends BeanBaseWizard {
	
	
	private BeanBrowseEditWizardPage page;
	private BeanBrowseEditBuilder builder;

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
		builder = new BeanBrowseEditBuilder();
		builder.setEditorName (page.getEditorNameText());
		builder.setCreatorName(page.getCreatorNameText ());
	}
	
	
	
	

}
