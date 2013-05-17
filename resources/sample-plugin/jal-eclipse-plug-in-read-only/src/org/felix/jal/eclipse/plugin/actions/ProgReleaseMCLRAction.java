package org.felix.jal.eclipse.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.felix.programmers.IProgrammer;
import org.felix.programmers.ProgrammerCommand;
import org.felix.programmers.ProgrammerFactory;

public class ProgReleaseMCLRAction extends ProgrammerActionBase {

	public void run(IAction action) {
		super.run(action);
		
		if (activeProject != null) {
			IProgrammer programmer = ProgrammerFactory.create();
			programmer.execute(activeProject, ProgrammerCommand.Run, null, null);
		}
	}
}