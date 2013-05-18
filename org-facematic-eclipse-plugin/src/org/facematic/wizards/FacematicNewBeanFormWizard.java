package org.facematic.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.facematic.plugin.utils.BeanFormBuilder;

public class FacematicNewBeanFormWizard extends Wizard implements INewWizard {

	private FacematicNewBeanFormWizardPage page;
	private ISelection selection;
	IProject targetProject;

	public FacematicNewBeanFormWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		targetProject = NewFacematicProjectWizard.findProject (selection.getFirstElement());
	}

	
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.facematic.plugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}
	
	private void doFinish(String className, String formName, String containerName, IProject project, IProgressMonitor monitor) throws CoreException {
				
		IFile result = null;
		try {
			BeanFormBuilder builder = new BeanFormBuilder(className, formName, containerName, project, targetProject);
			result = builder.process();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (result == null) return;

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		
		IContainer container = (IContainer) resource;
		
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		final IFile file = result;
		
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, org.facematic.facematic.editors.FmMvcEditor.class.getCanonicalName(), true);
				} catch (PartInitException e) {
				}
			}
		});
		
	}


	@Override
	public boolean performFinish() {
		final String className = page.getClassName();
		final String formName = page.getFormName();
		final String containerName = page.getContainerName();
		final IProject project = page.getProject();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
					try {
						doFinish(className, formName, containerName, project, monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
			}
		};
		
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public void addPages() {
		page = new FacematicNewBeanFormWizardPage(selection);
		addPage(page);
	}
	

}
