package org.facematic.facematic.editors;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

public class MyXmlEditor  extends StructuredTextEditor {
	FmMvcEditor parent;
	public MyXmlEditor (FmMvcEditor parent) {
		super ();
		this.parent = parent;
		StructuredTextViewerConfigurationXML t = new StructuredTextViewerConfigurationXML ();
		setSourceViewerConfiguration(t);
	}

	public FmMvcEditor getParent() {
		return parent;
	}

}
