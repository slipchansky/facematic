package org.facematic.wizards;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.facematic.plugin.utils.FmProjectSupport;


public class NewFacematicProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage _pageOne;
	FmProjectWizardPage _pageTwo;
	private IConfigurationElement _configurationElement;

	public NewFacematicProjectWizard() {
		setWindowTitle("New facematic project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		_pageOne = new WizardNewProjectCreationPage("Facematic Project Wizard");
		_pageOne.setTitle("Facematic Project Project");
		_pageOne.setDescription("Create Facematic project from scratch.");
		
		

		addPage(_pageOne);
		
		_pageTwo = new FmProjectWizardPage("Project options", _pageOne);
		_pageTwo.setTitle("Project options");
		_pageTwo.setDescription("Create Facematic project from scratch.");
		addPage (_pageTwo);
		
	}
	
	

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		_pageTwo.updateValues();
		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		String name = _pageOne.getProjectName();
		URI location = null;
		if (!_pageOne.useDefaults()) {
			location = _pageOne.getLocationURI();
		}
		
		Map<String, String> substs = new HashMap<String, String> () {{
			put ("GROUPID",          _pageTwo.getGROUPID());
			put ("GROUPID-dir",      _pageTwo.getGROUPID_DIR());
			put ("PROJECTNAME",      _pageOne.getProjectName());
			put ("ARTEFACTNAME",      _pageTwo.getArtefactName());
			put ("JAVA-VER", "1.6");
			put ("VERSION",          _pageTwo.getArtefactVersion ());
			put ("FACEMATICVERSION", "1.0.3-BETA");
			put ("UICLASS",          _pageTwo.getUICLASS());
			put ("THEMENAME",        _pageTwo.getTHEMENAME());
			put ("SERVLETCLASS",     _pageTwo.getSERVLETCLASS());
		}};

		try {
			FmProjectSupport.createProject(name, location, substs);
			BasicNewProjectResourceWizard.updatePerspective(_configurationElement);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		

		return true;
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
	    _configurationElement = config;
	}

}
