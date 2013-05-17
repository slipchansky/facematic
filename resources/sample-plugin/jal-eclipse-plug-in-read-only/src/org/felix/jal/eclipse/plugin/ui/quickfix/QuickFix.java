package org.felix.jal.eclipse.plugin.ui.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;

public class QuickFix implements IMarkerResolution {
	String label;
	
	public QuickFix(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		//TODO Implementar Quickfixers
		 MessageDialog.openInformation(null, "QuickFix Demo",
         "This quick-fix is not yet implemented");

	}

}
