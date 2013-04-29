package org.facematic.facematic.editors;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class MyJavaSourceViewerConfiguration extends JavaSourceViewerConfiguration {

	public MyJavaSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
		// TODO Auto-generated constructor stub
	}
	
	public MyJavaSourceViewerConfiguration(JavaTextTools tools, ITextEditor editor) {
		super(tools, editor);
		// TODO Auto-generated constructor stub
	}

}