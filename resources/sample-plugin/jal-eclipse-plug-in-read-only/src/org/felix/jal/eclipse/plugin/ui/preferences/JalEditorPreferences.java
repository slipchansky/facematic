package org.felix.jal.eclipse.plugin.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.felix.jal.eclipse.plugin.JalPlugin;

public class JalEditorPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public final static String DARKSYNHIGHLIGHTING = "Darksyntaxhighlighting";
	public final static String DATASHEETFOLDER = "datasheetFolder";
	public final static String LINKFILESFOLDER = "linkFilesFolder";
	public final static String COMMANDASSOCIATION = "commandAssociation";
	
	public JalEditorPreferences() {
		super("Editor", GRID);
		setPreferenceStore(JalPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();
		addField(new BooleanFieldEditor(DARKSYNHIGHLIGHTING, "Blue based color scheme", p));
		
		addField(new DirectoryFieldEditor(DATASHEETFOLDER, "Datasheets path", p));
		addField(new DirectoryFieldEditor(LINKFILESFOLDER, "Related files path", p));
		
		final StringFieldEditor editor = new StringFieldEditor("dummyCmdAssocEntry", "New association (ext:command)", p);
		addField(editor);
		
		addField(new ListEditor(COMMANDASSOCIATION, "File associations", p) {
			
			@Override
			protected String[] parseString(String stringList) {
				return stringList.split(";");
			}
			
			@Override
			protected String getNewInputObject() {
				String value = editor.getStringValue().trim();
				if (value.isEmpty() || value.indexOf(":")<0)
					return null;
				editor.setStringValue("");
				return value;
			}
			
			@Override
			protected String createList(String[] items) {
			    if (items.length == 0) return "";
			    StringBuffer buffer = new StringBuffer(items[0]);
			    for (int i=1; i<items.length; i++) {
			    	buffer.append(";");
			    	buffer.append(items[i]);
			    }
			    return buffer.toString();				
			}
		});
	}
	
	public static boolean isDarkSyntaxHighlightingEnabled() {
		return JalPlugin.getDefault().getPreferenceStore().getBoolean(DARKSYNHIGHLIGHTING);
	}

	public static String getDatasheetFolder() {
		return JalPlugin.getDefault().getPreferenceStore().getString(DATASHEETFOLDER);
	}

	public static String getLinkFilesFolder() {
		return JalPlugin.getDefault().getPreferenceStore().getString(LINKFILESFOLDER);
	}

	public static String getCommandFromExtension(String extension) {
		String s = JalPlugin.getDefault().getPreferenceStore().getString(COMMANDASSOCIATION);
		String[] commands = s.split(";");
		for (int i=0; i<commands.length; i++) 
			if (commands[i].startsWith(extension))
				return commands[i].split(":")[1];
		return null;
	}
}
