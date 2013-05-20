package org.facematic.wizards.beanwizard.base;

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
import org.facematic.wizards.NewFacematicProjectWizard;

public abstract class BeanBaseWizard extends Wizard implements INewWizard {

	protected BeanFormWizardPage mainWizardPage;
	protected ISelection  selection;
	
	protected IProject    targetProject;
	protected String entityBeanClassName;
	protected String controllerName;
	protected String destinationPackagePath;
	protected IProject entityClassProject;

	public BeanBaseWizard() {
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
	
	private void doFinish(IProgressMonitor monitor) throws CoreException {
				
		IFile resultFile = null;
		try {
			resultFile = processTemplate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (resultFile == null) return;

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(destinationPackagePath));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + destinationPackagePath + "\" does not exist.");
		}
		
		IContainer container = (IContainer) resource;
		
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		final IFile file = resultFile;
		
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

	/**
	 * @return
	 * @throws Exception
	 */
	protected IFile processTemplate() throws Exception {
		IFile resultFile;
		BeanFormBuilder builder = getBuilderInstance();
		builder.setBeanClassQualifiedName(entityBeanClassName);
		builder.setBeanFormControllerName(controllerName);
		builder.setClassProject(entityClassProject);
		builder.setTargetProject(targetProject);
		builder.setDestinationPackagePath(destinationPackagePath);
		resultFile = builder.process();
		return resultFile;
	}


	@Override
	public boolean performFinish() {
		
		prepareFinishContext();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
					try {
						
						doFinish(monitor);
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

	protected void prepareFinishContext() {
		entityBeanClassName = mainWizardPage.getEntityBeanClassName();
		controllerName = mainWizardPage.getControllerName();
		destinationPackagePath = mainWizardPage.getDestinationPackagePath();
		entityClassProject = mainWizardPage.getEntityClassProject();
	}
	
	@Override
	public void addPages() {
		mainWizardPage = newBeanFormWizardPage();
		addPage(mainWizardPage);
	}

	protected BeanFormWizardPage newBeanFormWizardPage() {
		return new BeanFormWizardPage (selection);
	}
	
	protected abstract BeanFormBuilder getBuilderInstance ();  

}
