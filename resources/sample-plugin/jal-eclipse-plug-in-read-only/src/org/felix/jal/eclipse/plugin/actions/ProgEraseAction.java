package org.felix.jal.eclipse.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.felix.programmers.IProgrammer;
import org.felix.programmers.ProgrammerCommand;
import org.felix.programmers.ProgrammerFactory;

public class ProgEraseAction extends ProgrammerActionBase {

	public void run(IAction action) {
		super.run(action);
		
		if (activeProject != null) {
			IProgrammer programmer = ProgrammerFactory.create();
					
		    if (programmer.execute(activeProject, ProgrammerCommand.Erase, null, null)==0)
		    	programmer.execute(activeProject, ProgrammerCommand.BlankCheck, null, null);
		}
	}
}