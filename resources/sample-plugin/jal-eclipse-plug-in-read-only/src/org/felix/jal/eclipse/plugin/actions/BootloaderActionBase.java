package org.felix.jal.eclipse.plugin.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class BootloaderActionBase implements IWorkbenchWindowActionDelegate  {
	protected IWorkbenchWindow window;
	protected IProject activeProject;
	
	@Override
	public void run(IAction action) {
		activeProject = null;

		IEditorPart  editorPart = window.getActivePage().getActiveEditor();

		IFile file = null;
		
		if(editorPart  != null)	{
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
		    file = input.getFile();
		    activeProject = file.getProject();
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
