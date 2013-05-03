package org.facematic.facematic.editors.parts;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.facematic.facematic.editors.FmMvcEditor;

public class FmJavaEditor extends CompilationUnitEditor implements HaveParent{
	

	private FmMvcEditor parent;

	public FmJavaEditor(FmMvcEditor parent) {
		super();
		this.parent = parent;
		IPreferenceStore pc = PreferenceConstants.getPreferenceStore();
		//org.eclipse.ui.preferences.
		
		JavaSourceViewerConfiguration myJavaSourceViewerConfiguration = new JavaSourceViewerConfiguration(new JavaColorManager(), pc, this, "__content_types_category");
		setSourceViewerConfiguration(myJavaSourceViewerConfiguration);
		
	}
	
	public void dispose() {
		super.dispose();
	}
	
	public FmMvcEditor getParent() {
		return parent;
	}
	
	

}
