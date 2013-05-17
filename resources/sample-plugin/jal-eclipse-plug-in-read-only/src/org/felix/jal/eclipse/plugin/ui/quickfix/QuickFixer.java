package org.felix.jal.eclipse.plugin.ui.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class QuickFixer implements IMarkerResolutionGenerator {

	@Override
	public IMarkerResolution[] getResolutions(IMarker mk) {
		 try {
	            Object problem = mk.getAttribute(IMarker.MESSAGE);
	            return new IMarkerResolution[] {
	               new QuickFix("Fix #1 for "+problem),
	               new QuickFix("Fix #2 for "+problem),
	            };
	         }
	         catch (CoreException e) {
	            return new IMarkerResolution[0];
	         }
	}

}
