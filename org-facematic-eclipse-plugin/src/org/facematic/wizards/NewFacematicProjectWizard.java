package org.facematic.wizards;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.facematic.Settings;
import org.facematic.plugin.utils.FmProjectSupport;
import org.facematic.plugin.utils.ProjectPomHelper;
import org.facematic.utils.StreamUtils;
import static org.facematic.Constants.*;


public class NewFacematicProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	
	
	private WizardNewProjectCreationPage pageNewProject;
	FmProjectWizardPage _pageTwo;
	private IConfigurationElement _configurationElement;
	private boolean implementToExistingProject;
	private String projectName;
	private URI projectUri;
	private ProjectPomHelper pom;
	private IProject project;

	public NewFacematicProjectWizard() {
		setWindowTitle("New facematic project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		project = findProject (selection.getFirstElement());
		pom = getMavenProject();
		defineProject(pom);
		
		_pageTwo = new FmProjectWizardPage("Project options", this);
		_pageTwo.setTitle("Project options");
		_pageTwo.setDescription("Create Facematic project from scratch.");
		addPage (_pageTwo);
		
	}
	
	private void addResourceOnFacematicImplementation (FmProjectSupport support) {
		File file = (File) project.getFile(".classpath");		
		if (file == null) {
			return;
		}
		try {
			InputStream is = file.getContents();
			String text = StreamUtils.getString(is);
			is.close();
			Document document = DocumentHelper.parseText(text);
			List<Element> classpathentries = document.getRootElement().elements("classpathentry");
			
			for (Element e : classpathentries) {
				if ("src/main/resources".equals(e.attributeValue("path")) && "src".equals(e.attributeValue("kind"))) {
					return;
				}
			}
			
			Element classPath = document.getRootElement().addElement("classpathentry");
			classPath.addAttribute("kind", "src");
			classPath.addAttribute("path", "src/main/resources");
			String classPathCode = document.asXML ();
			support.updateFile(".classpath", classPathCode);
			support.createProjectFolder("src/main/resources");
			
		} catch (Exception e) {
		}
		
	}

	private ProjectPomHelper getMavenProject() {
		if (project == null) {
			return null;
		}
		File file = (File) project.getFile("pom.xml");
		
		if (file == null) {
			return null;
		}
		
		try {
			InputStream is = file.getContents();
			ProjectPomHelper pom = new ProjectPomHelper (is);
			is.close();
			this.projectName = project.getProject().getName();
			IPath location = project.getProject().getLocation();
			java.io.File projectFile = location.toFile().getAbsoluteFile();
			this.projectUri = projectFile.toURI();
			
			return pom;
		} catch (Exception e) {
			return null;
		}
		
		
	}

	public static IProject findProject(Object selection) {
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
			put (CONTEXT_GROUPID,          _pageTwo.getGROUPID());
			put (CONTEXT_GROUPDIR,         _pageTwo.getGROUPID_DIR());
			put (CONTEXT_PROJECTNAME,      getProjectName());
			put (CONTEXT_ARTIFACTNAME,      _pageTwo.getArtefactName());
			put (CONTEXT_JAVAVERSION, "1.6");
			put (CONTEXT_VERSION,          _pageTwo.getArtefactVersion ());
			put (CONTEXT_FACEMATICVERSION, Settings.FACEMATIC_VERSION);
			put (CONTEXT_UICLASS,          _pageTwo.getUICLASS());
			put (CONTEXT_THEMENAME,        _pageTwo.getTHEMENAME());
			put (CONTEXT_SERVLETCLASS,     _pageTwo.getSERVLETCLASS());
		}};

		try {
			FmProjectSupport support = new FmProjectSupport (name, location, substs, implementToExistingProject, this.pom);
			
			support.createProject();
			if (implementToExistingProject) {
				addResourceOnFacematicImplementation (support);
			}
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
