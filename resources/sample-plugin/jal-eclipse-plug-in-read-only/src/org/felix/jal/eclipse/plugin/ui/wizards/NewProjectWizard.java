package org.felix.jal.eclipse.plugin.ui.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.felix.jal.eclipse.plugin.builder.JalNature;


public class NewProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage wizardPage;
	
	private TargetHardwareWizardPage targetHardPage;
	
	private JalLibrariesReferencePage projrefs;

	private IConfigurationElement config;

	private IWorkbench workbench;

	private IStructuredSelection selection;

	private IProject project;

	public NewProjectWizard() {
		super();
	}
	
	public void addPages() {
		wizardPage = new WizardNewProjectCreationPage("NewJALProject");
		wizardPage.setDescription("Create a new Jal Project.");
		wizardPage.setTitle("Jal Project");
		addPage(wizardPage);
				
		projrefs = new JalLibrariesReferencePage(selection);		
		addPage(projrefs);

		targetHardPage = new TargetHardwareWizardPage(selection);
		addPage(targetHardPage);
		
		projrefs.setTargetHardPage(targetHardPage);
	}
	
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	@Override
	public boolean performFinish() {
		if (project != null || !targetHardPage.isPageComplete() || !projrefs.isPageComplete()) {
			return true;
		}

		final IProject projectHandle = wizardPage.getProjectHandle();
		final String mcuMicro = targetHardPage.getMicro();
		final String mcuFreq = targetHardPage.getFrequency();


		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage
				.getLocationURI() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace.newProjectDescription(projectHandle.getName());

		desc.setLocationURI(projectURI);
		
		if (projrefs.mustCreateProject()) {
			final IProject projectRefHandle = workspace.getRoot().getProject(projrefs.getLibraryProjectName());
			final IProjectDescription descRefProj = workspace.newProjectDescription(projectRefHandle.getName());
						
			descRefProj.setLocation(new Path(projrefs.getLibraryPath()));

			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor)
						throws CoreException {
					createReferenceProject(descRefProj, projectRefHandle, monitor);
				}
			};
			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error", realException
						.getMessage());
				return false;
			}
			
			IProject[] projReferences = new IProject[1];
			projReferences[0] = projectRefHandle;
			desc.setReferencedProjects(projReferences);
		}
		else		
			desc.setReferencedProjects(projrefs.getReferencedProjects());
		
		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, monitor, mcuMicro, mcuFreq);
			}
		};

		/*
		 * This isn't as robust as the code in the BasicNewProjectResourceWizard
		 * class. Consider beefing this up to improve error handling.
		 */
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}

		project = projectHandle;

		if (project == null)
			return false;

		BasicNewProjectResourceWizard.updatePerspective(config);
		BasicNewProjectResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

		return true;
		
	}
	
	private void createReferenceProject(IProjectDescription description, IProject proj,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {

			monitor.beginTask("", 2000);

			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));

		} catch (Exception ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * This creates the project in the workspace.
	 * 
	 * @param description
	 * @param projectHandle
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	private void createProject(IProjectDescription description, IProject proj,
			IProgressMonitor monitor, String mcuMicro, String mcuFreq) throws CoreException,
			OperationCanceledException {
		try {

			monitor.beginTask("", 2000);

			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));

			/*
			 * Okay, now we have the project and we can do more things with it
			 * before updating the perspective.
			 */
			IContainer container = (IContainer) proj;

			/* Add an XHTML file */
			addFileToProject(container, new Path(proj.getName()+".jal"),
					openContentStream(proj.getName(), "", "", 
							mcuMicro, mcuFreq), monitor);

			toggleNature(proj);

		} catch (Exception ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}
	
	private void toggleNature(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (JalNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i,
							natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = JalNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}	
	
	public static InputStream openContentStream(String title, String description, String author,
			String picmicro, String frequency) throws CoreException {

		/* We want to be truly OS-agnostic */
		final String newline = System.getProperty("line.separator");

		String line;
		StringBuffer sb = new StringBuffer();

		try {
			InputStream input = NewProjectWizard.class.getResourceAsStream(
					"templates/main-source-template.resource");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			try {

				while ((line = reader.readLine()) != null) {
					line = line.replaceAll("\\$\\{title\\}", title);					
					line = line.replaceAll("\\$\\{description\\}", description);
					line = line.replaceAll("\\$\\{author\\}", author);
					line = line.replaceAll("\\$\\{picmicro\\}", picmicro);
					line = line.replaceAll("\\$\\{frequency\\}", frequency);
					line = line.replaceAll("\\$\\{datetime\\}", new java.util.Date().toString());
					
					sb.append(line);
					sb.append(newline);
				}

			} finally {
				reader.close();
			}

		} catch (IOException ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		}

		return new ByteArrayInputStream(sb.toString().getBytes());
	}	

	@Override
	public void setInitializationData(IConfigurationElement config, String arg1,
			Object arg2) throws CoreException {

		this.config = config;		
	}
	
	/**
	 * Adds a new file to the project.
	 * 
	 * @param container
	 * @param path
	 * @param contentStream
	 * @param monitor
	 * @throws CoreException
	 */
	private void addFileToProject(IContainer container, Path path,
			InputStream contentStream, IProgressMonitor monitor)
			throws CoreException {
		final IFile file = container.getFile(path);

		if (file.exists()) {
			file.setContents(contentStream, true, true, monitor);
		} else {
			file.create(contentStream, true, monitor);
		}
	}

}
