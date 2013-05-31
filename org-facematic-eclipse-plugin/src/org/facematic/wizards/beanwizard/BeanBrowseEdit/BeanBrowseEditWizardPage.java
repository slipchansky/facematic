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

	protected Text editorNameText;
	protected Text creatorNameText;

	public BeanBrowseEditWizardPage(ISelection selection) {
		super(selection);
	}

	@Override
	protected void addMoreControls (Composite container) {
		editorNameText = addInputLine(container, "&Details Controller Name:");
		creatorNameText = addInputLine (container, "C&reate Controller Name:");
	}

	protected Text addInputLine(Composite container, String inputCaption) {
		Label label = new Label(container, SWT.NULL);
		label.setText(inputCaption);
		Text inputLine = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		inputLine.setLayoutData(gd);
		new Label (container, SWT.NULL).setLayoutData(gd);
		return inputLine;
	}

	@Override
	protected void updateInputsForBeanClass(IType type) {
		controllerNameText.setText(type.getElementName()+"Browser");
		editorNameText.setText(type.getElementName()+"FormEdit");
		if (creatorNameText != null) creatorNameText.setText(type.getElementName()+"FormCreate");
	}

	public String getEditorNameText() {
		return editorNameText.getText();
	}
	
	public String getCreatorNameText () {
		if (creatorNameText==null) return "";
		return creatorNameText.getText();
	}
}
