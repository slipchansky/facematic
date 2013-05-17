package org.felix.jal.eclipse.plugin.ui.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.felix.jal.eclipse.plugin.JalPlugin;

public class BootloaderPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public final static String PICDEM_BOOTLOADER = "fsusb";

	public final static String SELECTED_BOOTLOADER = "selectedBootloader";
	private final static String DEFAULT_BOOTLOADER = PICDEM_BOOTLOADER;

	private final static String DEFAULT_BOOTLOADER_PATH = "fsusb.exe";
	public final static String BOOTLOADER_PATH = "bootloaderPath";
	
	public BootloaderPreferences() {
		super("Bootloader", GRID);
		setPreferenceStore(JalPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
		
	}
	
	@Override
	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();
		String[][] bootloaders = new String[][] {
				{"PICDEM (fsusb)", PICDEM_BOOTLOADER},
		};
		
		addField(new ComboFieldEditor(SELECTED_BOOTLOADER, "Select bootloader", bootloaders, p));

		addField(new FileFieldEditor(BOOTLOADER_PATH, "FSUSB path", p));		
	}

	public static String getBootloaderPath() {
		String bootloaderPath = JalPlugin.getDefault().getPreferenceStore().getString(BOOTLOADER_PATH);
		if ("".equals(bootloaderPath))
			bootloaderPath = DEFAULT_BOOTLOADER_PATH;
		
		return bootloaderPath;
	}

	public static String getBootloader() {
		String value = JalPlugin.getDefault().getPreferenceStore().getString(SELECTED_BOOTLOADER);
		if ("".equals(value))
			value = DEFAULT_BOOTLOADER;
		
		return value;
	}
	
}
