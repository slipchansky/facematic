package org.facematic.wizards.beanwizard.BeanForm;

import org.eclipse.core.resources.IFile;
import org.facematic.wizards.beanwizard.base.BeanBaseWizard;
import org.facematic.wizards.beanwizard.base.BeanFormBuilder;
import org.facematic.wizards.beanwizard.base.BeanFormWizardPage;

public class FacematicNewBeanFormWizard extends BeanBaseWizard {
	

	@Override
	protected BeanFormWizardPage newBeanFormWizardPage() {
		return super.newBeanFormWizardPage();
	}

	
	@Override
	protected BeanFormBuilder getBuilderInstance() {
		return new BeanFormBuilder();
	}

}
