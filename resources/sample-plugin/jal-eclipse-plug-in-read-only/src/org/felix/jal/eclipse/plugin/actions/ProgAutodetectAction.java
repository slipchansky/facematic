package org.felix.jal.eclipse.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.felix.jal.eclipse.plugin.JalPlugin;
import org.felix.programmers.IProgrammer;
import org.felix.programmers.ProgrammerCommand;
import org.felix.programmers.ProgrammerFactory;

public class ProgAutodetectAction extends ProgrammerActionBase {

	public void run(IAction action) {
		super.run(action);
		
		if (activeProject != null) {
			IProgrammer programmer = ProgrammerFactory.create();
			
			if (programmer.execute(activeProject, ProgrammerCommand.Connect, null, null)!=0)
				JalPlugin.getDefault().setCurrentMCU(null);	    	
		}
	}
}