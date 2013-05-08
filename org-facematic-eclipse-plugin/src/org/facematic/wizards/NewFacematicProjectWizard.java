package org.facematic.wizards;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.facematic.facematic.editors.FmMvcEditor;
import org.facematic.plugin.utils.FmProjectSupport;
import org.facematic.plugin.utils.ProjectPomHelper;


public class NewFacematicProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage pageNewProject;
	FmProjectWizardPage _pageTwo;
	private IConfigurationElement _configurationElement;
	private boolean implementToExistingProject;
	private String projectName;
	private URI projectUri;
	private ProjectPomHelper pom;

	public NewFacematicProjectWizard() {
		setWindowTitle("New facematic project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		pom = getMavenProject(selection.getFirstElement());
		defineProject(pom);
		
		_pageTwo = new FmProjectWizardPage("Project options", this);
		_pageTwo.setTitle("Project options");
		_pageTwo.setDescription("Create Facematic project from scratch.");
		addPage (_pageTwo);
		
	}

	private ProjectPomHelper getMavenProject(Object selection) {
		IProject jprj = findProject (selection);
		if (jprj==null) return null;
		File file = (File) jprj.getFile("pom.xml");
		
		if (file == null) {
			return null;
		}
		
		try {
			InputStream is = file.getContents();
			ProjectPomHelper pom = new ProjectPomHelper (is);
			is.close();
			this.projectName = jprj.getProject().getName();
			IPath location = jprj.getProject().getLocation();
			java.io.File projectFile = location.toFile().getAbsoluteFile();
			this.projectUri = projectFile.toURI();
			
			return pom;
		} catch (Exception e) {
			return null;
		}
		
		
	}

	private IProject findProject(Object selection) {
		if (selection instanceof Project) {
			return (Project)selection;
		}
		if (selection instanceof JavaProject) {
			return ((JavaProject) selection).getProject();
		} else if (selection instanceof IJavaElement ) {
			return findProject (((IJavaElement)selection).getParent ());
		} else if (selection instanceof IContainer) {
			return findProject (((IContainer)selection).getParent ());
		}
		else return null;
	}
	

	private void defineProject(ProjectPomHelper pom) {
		
		if (pom != null && pom.isWar() && !pom.isFacematic()) {
			implementToExistingProject = true;
			return;
		}
		
		pageNewProject = new WizardNewProjectCreationPage("Facematic Project Wizard");
		pageNewProject.setTitle("New Facematic project");
		pageNewProject.setDescription("Create Facematic project from scratch.");
		addPage(pageNewProject);
	}
	
	

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		_pageTwo.updateValues();
		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		String name = getProjectName();
		URI location = getProjectLocation();
		
		Map<String, String> substs = new HashMap<String, String> () {{
			put ("GROUPID",          _pageTwo.getGROUPID());
			put ("GROUPDIR",         _pageTwo.getGROUPID_DIR());
			put ("PROJECTNAME",      getProjectName());
			put ("ARTEFACTNAME",      _pageTwo.getArtefactName());
			put ("JAVAVERSION", "1.6");
			put ("VERSION",          _pageTwo.getArtefactVersion ());
			put ("FACEMATICVERSION", "1.0.3-BETA");
			put ("UICLASS",          _pageTwo.getUICLASS());
			put ("THEMENAME",        _pageTwo.getTHEMENAME());
			put ("SERVLETCLASS",     _pageTwo.getSERVLETCLASS());
			put ("GENERATED_CODE_BEGIN", FmMvcEditor.GENERATED_CODE_BEGIN);
			put ("GENERATED_CODE_END", FmMvcEditor.GENERATED_CODE_END);
		}};

		try {
			FmProjectSupport support = new FmProjectSupport (name, location, substs, implementToExistingProject, this.pom);
			support.createProject();
			BasicNewProjectResourceWizard.updatePerspective(_configurationElement);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		

		return true;
	}

	private URI getProjectLocation() {
		if (this.implementToExistingProject) {
			return this.projectUri;
		}
		
		
		URI location = null;
		if (!pageNewProject.useDefaults()) {
			location = pageNewProject.getLocationURI();
		}
		return location;
	}

	public String getProjectName() {
		if (this.implementToExistingProject)
			return this.projectName;
		
		return pageNewProject.getProjectName();
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
	    _configurationElement = config;
	}
	
	public boolean isImplementToExistingProject () {
		return implementToExistingProject;
	}

	public ProjectPomHelper getPom() {
		return pom;
	}


}
