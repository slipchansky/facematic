package org.felix.bootloaders;

import org.felix.jal.eclipse.plugin.ui.preferences.BootloaderPreferences;


public class BootloaderFactory {
	public static IBootloader create() {
		String bootloader = BootloaderPreferences.getBootloader();
		
		if (bootloader.equals(BootloaderPreferences.PICDEM_BOOTLOADER))
			return new USBBootloader();

		//TODO Implementar bootloader serial
		
		return null;
	}
}
