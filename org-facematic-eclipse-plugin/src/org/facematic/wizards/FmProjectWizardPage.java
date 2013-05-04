package org.facematic.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class FmProjectWizardPage extends WizardPage  {

	private Text groupText;
	private Text artefactText;
	private Text servletNameText;
	private Text uiNameText;
	private WizardNewProjectCreationPage projectPage;
	private Text skinNameText;
	private Text artefactVersionText;

	protected FmProjectWizardPage(String pageName, WizardNewProjectCreationPage projectPage) {
		super(pageName);
		this.projectPage = projectPage;
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Group ID:");
		groupText = new Text(container, SWT.BORDER | SWT.SINGLE);
		if (groupText.getText()==null || "".equals(groupText.getText ().trim())) {
		    groupText.setText("org.myfm");
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		groupText.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Artefact ID:");
		artefactText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		artefactText.setLayoutData(gd);
		artefactText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateArtefactNameDependentValues();
			}
		});
		
		
		
		label = new Label(container, SWT.NULL);
		label.setText("&Version:");
		artefactVersionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		artefactVersionText.setLayoutData(gd);
		artefactVersionText.setText("0.0.1-SNAPSHOT");
		
		
		
		label = new Label(container, SWT.NULL);
		label.setText("&Servlet ClassName:");
		servletNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		servletNameText.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("&UI ClassName:");
		uiNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		uiNameText.setLayoutData(gd);
		
		
		label = new Label(container, SWT.NULL);
		label.setText("&Skin name:");
		skinNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		skinNameText.setLayoutData(gd);
		
		setControl(container);
	}
	
	public void updateValues () {
		String projectName = projectPage.getProjectName();
		artefactText.setText (projectName.toLowerCase());
		updateArtefactNameDependentValues ();
		
	}
	
	private void updateArtefactNameDependentValues() {
		String artefactName = artefactText.getText();
		servletNameText.setText(artefactName.substring(0, 1).toUpperCase()+artefactName.substring(1).toLowerCase()+"Servlet");
		uiNameText.setText(artefactName.substring(0, 1).toUpperCase()+artefactName.substring(1).toLowerCase()+"UI");
		skinNameText.setText(artefactName.toLowerCase());
	}

	public String getGROUPID () {
		return groupText.getText ();
	}

	public String getGROUPID_DIR () {
		return groupText.getText ().replace('.', '/');
	}

	public String getSERVLETCLASS () {
		return servletNameText.getText ();
	}
	public String getUICLASS () {
		return uiNameText.getText ();
	}
	public String getTHEMENAME () {
		return skinNameText.getText ();
	}

	public String getArtefactVersion() {
		return artefactVersionText.getText();
	}
	
	public String getArtefactName() {
		return artefactText.getText();
	}
	


}
