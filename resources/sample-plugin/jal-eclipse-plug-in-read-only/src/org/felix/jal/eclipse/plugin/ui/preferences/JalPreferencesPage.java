package org.felix.jal.eclipse.plugin.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.felix.jal.eclipse.plugin.JalPlugin;


public class JalPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{
	private final static String DEFAULT_COMPILER_PATH = "Jalv2.exe"; 
	public final static String COMPILER_PATH = "compilerPath";
	public final static String COMPILER_OPTIONS = "compilerOptions";
	
	public JalPreferencesPage() {
		super("General", GRID);
		setPreferenceStore(JalPlugin.getDefault().getPreferenceStore());
	}
	
	
	protected void createFieldEditors() {
		
		Composite p = getFieldEditorParent();
		addField(new FileFieldEditor(COMPILER_PATH, "Compiler path", p));
        addField(new StringFieldEditor(COMPILER_OPTIONS,"Compiler options",p));
		
	}

	public void init(IWorkbench workbench) {
		
	}
	
	public static String getCompilerPath() {
		String compilerPath=  JalPlugin.getDefault().getPreferenceStore().getString(COMPILER_PATH);
		if ("".equals(compilerPath )) {
			compilerPath = DEFAULT_COMPILER_PATH;
		}
		return compilerPath;
	}
	public static String getCompilerOptions() {
		return JalPlugin.getDefault().getPreferenceStore().getString(COMPILER_OPTIONS);
	}

}
