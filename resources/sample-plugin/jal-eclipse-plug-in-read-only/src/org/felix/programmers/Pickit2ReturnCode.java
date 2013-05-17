package org.felix.programmers;

public class Pickit2ReturnCode {
	
	private int returnCode;
	
	public Pickit2ReturnCode(Integer value) {
		returnCode = value;
	}
	
	public String toString() {
		switch (returnCode) {
		case 0: return "OK.";
		case 5: return "ERROR: A Vdd and/or Vpp voltage error was detected.\nThis could be due to PICkit 2 being improperly connected to a part, incorrect part selection, or interference from other circuitry on the target board.";
		case 7: return "ERROR: Operation failed.";
		case 10: return "ERROR: The PK2CMD executable is unable to find a connected PICkit 2 programmer.";
		case 11: return "ERROR: The OS firmware must be updated before being used with this version of PK2CMD.";
		case 15: return "ERROR: A file specified for reading to (-gf...) cannot be opened for writing.";
		case 24: return "ERROR: The PK2CMD executable cannot find the device file PK2DeviceFile.dat or the device file may be corrupted.";
		case 28: return "ERROR: OS firmware upgade (-d...) fails.";
		case 34: return "ERROR: A program or verify operation fails.";
		case 36: return "ERROR: Syntax error in a command line argument was detected, an invalid combination of operations was entered, an invalid value was entered, or a memory region was selected that is not supported by the current device.";
		case 37: return "ERROR: Error opening or loading a specified hex file (-f...)";
		case 39: return "ERROR: A part autodetect operation failed to find a known part";
		}
		return "ERROR: Unknown result state";
	}
}
