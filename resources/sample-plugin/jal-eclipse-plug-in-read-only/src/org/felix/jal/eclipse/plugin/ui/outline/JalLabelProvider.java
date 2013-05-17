package org.felix.jal.eclipse.plugin.ui.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.felix.jal.eclipse.plugin.JalPlugin;
import org.felix.jal.lang.JalContainer;
import org.felix.jal.lang.JalElement;
import org.felix.jal.lang.JalInclude;
import org.felix.jal.lang.JalMethod;
import org.felix.jal.lang.JalVariable;


public class JalLabelProvider extends LabelProvider {

	public String getText(Object item) {
		if (item instanceof JalElement) {
			JalElement named = (JalElement) item;
			return named.getTypedName();
		}
		return item.toString();
	}
	/*
	 * http://help.eclipse.org/help31/index.jsp?topic=/org.eclipse.jdt.doc.user/reference/ref-156.htm
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object item) {
		String image = null;
		if (item instanceof JalMethod) {
			
			JalMethod m = (JalMethod) item;
			if (m.getName().indexOf('\'')!=-1) {
				return new Image(Display.getCurrent(), JalPlugin.class
					.getResourceAsStream("methpro_obj.png"));
			} else if (m.getName().startsWith("_")) {
				return new Image(Display.getCurrent(), JalPlugin.class
					.getResourceAsStream("methpri_obj.png"));
			} else {
				return new Image(Display.getCurrent(), JalPlugin.class
						.getResourceAsStream("methpub_obj.png"));
			}
		}
		
		if (item instanceof JalVariable) {
			
			return new Image(Display.getCurrent(), JalPlugin.class
						.getResourceAsStream("var_obj.png"));
		}		
		
		if (item instanceof JalContainer) { 
			JalContainer c = (JalContainer) item;
			if ("includes".equals(c.getName())) {
				return new Image(Display.getCurrent(), JalPlugin.class
						.getResourceAsStream("impc_obj.png"));
			} else {
				image = org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER;
			}
			
		}
		
		if (item instanceof JalInclude) 
			return new Image(Display.getCurrent(), JalPlugin.class
					.getResourceAsStream("include.png"));
		
		if (image!=null)
			return PlatformUI.getWorkbench().getSharedImages().getImage(image);

		return null;
	}

}
