package org.felix.jal.eclipse.plugin.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.felix.programmers.IProgrammer;
import org.felix.programmers.ProgrammerCommand;
import org.felix.programmers.ProgrammerFactory;

public class ProgReadAction extends ProgrammerActionBase {

	public void run(IAction action) {
		super.run(action);

		if (activeProject != null) {
			IProgrammer programmer = ProgrammerFactory.create();
			
			if (programmer.execute(activeProject, ProgrammerCommand.Read, null, null)==0) {
				
				IPath location = new Path(programmer.getTempHexFile().getPath());
				IFile ifile = activeProject.getFile(location.lastSegment());
				try {
					ifile.createLink(location, IResource.NONE, null);
					
					IWorkbenchPage page = window.getActivePage();
					if (page != null)
						try {
							IDE.openEditor(page, ifile, true);
						} catch (PartInitException e) {
							e.printStackTrace();
						}					
				} catch (CoreException e1) {
					e1.printStackTrace();
				}

			}
		}
	}
}