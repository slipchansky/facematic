package org.felix.programmers;

import org.felix.jal.eclipse.plugin.ui.preferences.ProgrammerPreferences;

public class ProgrammerFactory {
	public static IProgrammer create() {
		String programmer = ProgrammerPreferences.getProgrammer();
		
		if (programmer.equals(ProgrammerPreferences.PICKIT2_PROGRAMMER))
			return new Pickit2();
		
		if (programmer.equals(ProgrammerPreferences.PIKLABPROG_PROGRAMMER))
			return new PiklabProg();
		
		return null;
	}
}
