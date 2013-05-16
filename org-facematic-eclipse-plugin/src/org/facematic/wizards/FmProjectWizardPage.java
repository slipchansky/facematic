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
	private NewFacematicProjectWizard projectPage;
	private Text skinNameText;
	private Text artefactVersionText;

	protected FmProjectWizardPage(String pageName, NewFacematicProjectWizard newFacematicProjectWizard) {
		super(pageName);
		this.projectPage = newFacematicProjectWizard;
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
		
		if (projectPage.isImplementToExistingProject()) {
			artefactVersionText.setText(projectPage.getPom().getVersion());
			artefactVersionText.setEditable(false);
			groupText.setText(projectPage.getPom().getGroupId());
			groupText.setEditable(false);
			artefactText.setText(projectPage.getPom().getArtifactId());
			artefactText.setEditable(false);
		}
		
		setControl(container);
	}
	
	public void updateValues () {
		String projectName = projectPage.getProjectName();
		artefactText.setText (projectName.toLowerCase());
		updateArtefactNameDependentValues ();
		
	}
	
	private void updateArtefactNameDependentValues() {
		String artIfactName = artefactText.getText();
		if (artIfactName == null) {
			artIfactName = "noname";
		}
		artIfactName = updateArtifactName (artIfactName, "-");
		artIfactName = updateArtifactName (artIfactName, "\\.");
		
		servletNameText.setText(artIfactName.substring(0, 1).toUpperCase()+artIfactName.substring(1)+"Servlet");
		uiNameText.setText(artIfactName.substring(0, 1).toUpperCase()+artIfactName.substring(1)+"UI");
		skinNameText.setText(artIfactName.toLowerCase());
	}

	private static String updateArtifactName(String artIfactName, String c) {
		if (c==null || artIfactName==null)
			return artIfactName;
		String a[] = artIfactName.split(c);
		if (a.length<2) return artIfactName;
		artIfactName = a[0];
		for (int i=1; i< a.length; i++) {
			artIfactName+=a[i].substring(0, 1).toUpperCase()+a[i].substring(1);
		}
		
		return artIfactName;
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
