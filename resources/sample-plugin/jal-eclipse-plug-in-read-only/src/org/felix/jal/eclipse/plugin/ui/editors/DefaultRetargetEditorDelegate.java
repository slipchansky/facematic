package org.felix.jal.eclipse.plugin.ui.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

public class DefaultRetargetEditorDelegate extends ActionDelegate implements
		IWorkbenchWindowActionDelegate, IEditorActionDelegate {
	private IWorkbenchWindow window = null;
	private IEditorPart editor = null;
	private IAction action;
	
    public final void run(IAction action)
    {
    	this.action = action;
    	if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IEditorPart edt = page.getActiveEditor();
				if (edt != null && edt instanceof JalEditor) {
					this.editor = edt;
					doIt();
			        return;
				}
			}
    	}
    	else
    		doIt();
    }
    
    private void doIt() {
		JalEditor edt = (JalEditor) this.editor;				
        IAction toRun = edt.getAction(action.getId());
        assert toRun != null;

        toRun.run();    	
    }

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.action = action;
		this.editor = targetEditor;
		
	}



}
