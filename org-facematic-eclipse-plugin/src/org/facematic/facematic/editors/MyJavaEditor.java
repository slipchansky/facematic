package org.facematic.facematic.editors;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

public class MyJavaEditor extends CompilationUnitEditor {

	public MyJavaEditor() {
		super();
		JavaTextTools tools = new JavaTextTools (PreferenceConstants.getPreferenceStore());
		MyJavaSourceViewerConfiguration myJavaSourceViewerConfiguration = new MyJavaSourceViewerConfiguration(tools, this);
		setSourceViewerConfiguration(myJavaSourceViewerConfiguration);
	}
	
	public void dispose() {
		super.dispose();
	}

}
