package org.felix.jal.eclipse.plugin.ui.outline;

import java.util.Collections;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.felix.jal.lang.JalContainer;
import org.felix.jal.lang.JalElement;


public class JalContentProvider implements ITreeContentProvider{
	public Object[] getChildren(Object obj) {
		if (obj instanceof JalContainer) {
			JalContainer jfile = (JalContainer) obj;
			
			if (jfile.getName() != "root")
			Collections.sort(jfile.getElements());
			
			return (Object[]) jfile.getElements().toArray(new Object[jfile.getElements().size()]);
		}
		return null;
	}

	public Object getParent(Object obj) {
		if (obj instanceof JalElement) {
			JalElement elem = (JalElement) obj;
			return elem.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object obj) {
		if (obj instanceof JalContainer) {
			JalContainer jfile = (JalContainer) obj;
			return (jfile.getElements().size() >0);
		}
		return false;
	}

	public Object[] getElements(Object obj) {
		return getChildren(obj);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    ((TreeViewer)viewer).refresh(newInput, true);
	}
	

}
