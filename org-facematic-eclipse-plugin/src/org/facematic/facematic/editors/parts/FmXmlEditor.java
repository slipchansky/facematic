package org.facematic.facematic.editors.parts;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.facematic.facematic.editors.FmMvcEditor;

public class FmXmlEditor  extends StructuredTextEditor implements HaveParent{
	FmMvcEditor parent;
	public FmXmlEditor (FmMvcEditor parent) {
		super ();
		
		this.parent = parent;
		StructuredTextViewerConfigurationXML t = new StructuredTextViewerConfigurationXML ();
		setSourceViewerConfiguration(t);
	}

	public FmMvcEditor getParent() {
		return parent;
	}
	

}
