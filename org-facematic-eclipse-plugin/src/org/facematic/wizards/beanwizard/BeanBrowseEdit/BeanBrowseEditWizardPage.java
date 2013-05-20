package org.facematic.wizards.beanwizard.BeanBrowseEdit;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.facematic.wizards.beanwizard.base.BeanFormWizardPage;

public class BeanBrowseEditWizardPage extends BeanFormWizardPage {

	private Text editorNameText;
	private Text creatorNameText;

	public BeanBrowseEditWizardPage(ISelection selection) {
		super(selection);
	}

	@Override
	protected void addMoreControls (Composite container) {
		//super.addMoreControls (container);
		Label label = new Label(container, SWT.NULL);
		label.setText("&Details Controller Name:");
		editorNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		editorNameText.setLayoutData(gd);
		new Label (container, SWT.NULL).setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("C&reate Controller Name:");
		creatorNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		creatorNameText.setLayoutData(gd);
		new Label (container, SWT.NULL).setLayoutData(gd);
		
		
	}

	@Override
	protected void updateInputsForBeanClass(IType type) {
		controllerNameText.setText(type.getElementName()+"Browser");
		editorNameText.setText(type.getElementName()+"FormEdit");
		creatorNameText.setText(type.getElementName()+"FormCreate");
	}

	public String getEditorNameText() {
		return editorNameText.getText();
	}
	
	public String getCreatorNameText () {
		return creatorNameText.getText();
	}
}
