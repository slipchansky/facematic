package org.felix.jal.eclipse.plugin.ui.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.felix.jal.eclipse.plugin.JalPlugin;

public class ProgrammerPreferences extends  FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public final static String PICKIT2_PROGRAMMER = "pickit2";
	public final static String PIKLABPROG_PROGRAMMER = "piklab-prog";
	
	public final static String SELECTED_PROGRAMMER = "selectedProgrammer";
	private final static String DEFAULT_PROGRAMMER = PICKIT2_PROGRAMMER;

	private final static String DEFAULT_PROGRAMMER_PATH = "pk2cmd.exe";
	private final static String DEFAULT_PROGRAMMER_DATFILE = "PK2DeviceFile.dat";
	public final static String PROGRAMMER_PATH = "programmerPath";
	public final static String PROGRAMMER_DATFILE = "programmerDatFile";
		
	private final static String DEFAULT_PIKLABPROG_PATH = "piklab-prog";
	public final static String PIKLABPROG_PATH = "piklabprogPath";
	public final static String PIKLABPROG_FIRMWAREDIR = "piklabprogFirmwareDir";

	
	public ProgrammerPreferences() {
		super("Programmer", GRID);
		setPreferenceStore(JalPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
	
	}

	@Override
	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();
        
		String[][] programmers = new String[][] {
			{"Pickit 2", PICKIT2_PROGRAMMER},
			{"Piklab-prog (ICD2 usb)", PIKLABPROG_PROGRAMMER}
		};
		
		addField(new ComboFieldEditor(SELECTED_PROGRAMMER, "Select programmer", programmers, p));

        addField(new FileFieldEditor(PROGRAMMER_PATH, "Pickit 2 path", p));
        addField(new DirectoryFieldEditor(PROGRAMMER_DATFILE,"PK2DeviceFile.dat path",p));        

        addField(new FileFieldEditor(PIKLABPROG_PATH, "Piklab-prog path", p));
        addField(new DirectoryFieldEditor(PIKLABPROG_FIRMWAREDIR, "Firmware-dir", p));
	}
	
	public static String getProgrammerPath() {
		String value =  JalPlugin.getDefault().getPreferenceStore().getString(PROGRAMMER_PATH);
		if ("".equals(value )) {
			value = DEFAULT_PROGRAMMER_PATH;
		}
		return value;
	}
	
	public static String getProgrammerDatFilePath() {
		String value = JalPlugin.getDefault().getPreferenceStore().getString(PROGRAMMER_DATFILE);
		if ("".equals(value )) {
			value = DEFAULT_PROGRAMMER_DATFILE;
		}
		return value;
	}
	
	public static String getProgrammer() {
		String value = JalPlugin.getDefault().getPreferenceStore().getString(SELECTED_PROGRAMMER);
		if ("".equals(value )) {
			value = DEFAULT_PROGRAMMER;
		}
		return value;
	}	

	public static String getPiklabProgPath() {
		String value =  JalPlugin.getDefault().getPreferenceStore().getString(PIKLABPROG_PATH);
		if ("".equals(value )) {
			value = DEFAULT_PIKLABPROG_PATH;
		}
		return value;
	}
	
	public static String getPiklabProgFirmwareDir() {
		return JalPlugin.getDefault().getPreferenceStore().getString(PIKLABPROG_FIRMWAREDIR);
	}
}
