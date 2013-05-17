package org.felix.programmers;

/*
                        PICkit 2 COMMAND LINE HELP
Options              Description                              Default
----------------------------------------------------------------------------
A<value>             Set Vdd voltage                          Device Specific
B<path>              Specify the path to PK2DeviceFile.dat    Searches PATH
                                                              and calling dir
C                    Blank Check Device                       No Blank Check
D<file>              OS Download                              None
E                    Erase Flash Device                       Do Not Erase
F<file>              Hex File Selection                       None
G<Type><range/path>  Read functions                           None
                     Type F: = read into hex file,
                             path = full file path,
                             range is not used
                     Types P,E,I,C: = ouput read of Program,
                             EEPROM, ID and/or Configuration
                             Memory to the screen. P and E
                             must be followed by an address
                             range in the form of x-y where
                             x is the start address and y is
                             the end address both in hex,
                             path is not used
                             (Serial EEPROM memory is 'P')
H<value>             Delay before Exit                        Exit immediately
                         K = Wait on keypress before exit
                         1 to 9 = Wait <value> seconds
                                  before exit
I                    Display Device ID & silicon revision     Do Not Display
J<newlines>          Display operation percent complete       Rotating slash
                         N = Each update on newline
K                    Display Hex File Checksum                Do Not Display
L<rate>              Set programming speed                    Fastest
                     <rate> is a value of 1-16, with 1 being
                     the fastest.
M<memory region>     Program Device                           Do Not Program
                     memory regions:
                         P = Program memory
                         E = EEPROM
                         I = ID memory
                         C = Configuration memory
                         If no region is entered, the entire
                         device will be erased & programmed.
                         If a region is entered, no erase
                         is performed and only the given
                         region is programmed.
                         All programmed regions are verified.
			            (serial EEPROM memory is 'P')
N<string>            Assign Unit ID string to first found     None
                     PICkit 2 unit.  String is limited to 14
                     characters maximum.  May not be used
                     with other options.
                     Example: -NLab1B
P<part>              Part Selection. Example: -PPIC16f887     (Required)
P                    Auto-Detect in all detectable families
PF                   List auto-detectable part families
PF<id>               Auto-Detect only within the given part
                     family, using the ID listed with -PF
                     Example: -PF2
Q                    Disable PE for PIC24/dsPIC33 devices     Use PE
R                    Release /MCLR after operations           Assert /MCLR
S<string/#>          Use the PICkit 2 with the given Unit ID  First found unit
                     string.  Useful when multiple PICkit 2
                     units are connected.
                     Example: -SLab1B
                     If no <string> is entered, then the
                     Unit IDs of all connected units will be
                     displayed.  In this case, all other 
                     options are ignored. -S# will list units
                     with their firmware versions.
                     See help -s? for more info.
T                    Power Target after operations            Vdd off
U<value>             Program OSCCAL memory, where:            Do Not Program
                      <value> is a hexadecimal number
                      representing the OSCCAL value to be
                      programmed. This may only be used in
                      conjunction with a programming 
                      operation.
V<value>             Vpp override                             Device Specific
W                    Externally power target                  Power from Pk2
X                    Use VPP first Program Entry Method       VDD first
Y<memory region>     Verify Device                            Do Not Verify
                         P = Program memory
                         E = EEPROM
                         I = ID memory
                         C = Configuration memory
                         If no region is entered, the entire
                         device will be verified.
                         (Serial EEPROM memory is 'P')
Z                    Preserve EEData on Program               Do Not Preserve
?                    Help Screen                              Not Shown

     Each option must be immediately preceeded by a switch, Which can
     be either a dash <-> or a slash </> and options must be separated
     by a single space.

     Example:   PK2CMD /PPIC16F887 /Fc:\mycode /M
                               or
                PK2CMD -PPIC16F887 -Fc:\mycode -M

     Any option immediately followed by a question mark will invoke
     a more detailed description of how to use that option.

     Commands and their parameters are not case sensitive. Commands will
     be processed according to command order of precedence, not the order
     in which they appear on the command line. 
	Precedence:
                -?      (first)
                -B
                -S
                -D
                -N
                -P
                -A -F -J -L -Q -V -W -X -Z
                -C
                -U
                -E
                -M
                -Y
                -G
                -I -K
                -R -T
                -H      (last)
		
     The program will return an exit code upon completion which will
     indicate either successful completion, or describe the reason for
     failure. To view the list of exit codes and their descriptions,
     type -?E on the command line.

     type -?V on the command line for version information.

     type -?L on the command line for license information.

     type -?P on the command line for a listing of supported devices.
     type -?P<string> to search for and display a list of supported devices
                      beginning with <string>.

     Special thanks to the following individuals for their critical
     contributions to the development of this software:
		Jeff Post, Xiaofan Chen, and Shigenobu Kimura


PK2CMD return codes:
Value   Code                    Notes
-----   ----                    -----
0       OPSUCCESS              -Returned if all selected operations complete
                                successfully.
5       VOLTAGE_ERROR          -A Vdd and/or Vpp voltage error was detected.
                                This could be due to PICkit 2 being 
                                improperly connected to a part, incorrect
                                part selection, or interference from other
                                circuitry on the target board.
7       OPFAILURE              -Returned if an operation fails and a more
                                specific error does not exist.
10      NO_PROGRAMMER          -The PK2CMD executable is unable to find a
                                connected PICkit 2 programmer.
11      WRONG_OS                -Returned if the OS firmware must be updated
                                before being used with this version of
                                PK2CMD.
15      FILE_OPEN_ERROR        -Returned if a file specified for reading to
                                (-gf...) cannot be opened for writing.
24      DEVICEFILE_ERROR       -The PK2CMD executable cannot find the device
                                file PK2DeviceFile.dat or the device file
                                may be corrupted.
28      UPDGRADE_ERROR         -Returned when an OS firmware upgade (-d...)
                                fails.
34      PGMVFY_ERROR           -Returned if a program or verify operation
                                fails.
36      INVALID_CMDLINE_ARG    -A syntax error in a command line argument
                                was detected, an invalid combination of 
                                operations was entered, an invalid value was
                                entered, or a memory region was selected
                                that is not supported by the current device.
37      INVALID_HEXFILE        -Error opening or loading a specified hex
                                file (-f...).
39      AUTODETECT_FAILED       A part autodetect operation failed to find
                                a known part.
 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.felix.jal.eclipse.plugin.JalPlugin;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.preferences.ProgrammerPreferences;
import org.felix.jal.eclipse.plugin.ui.views.CompilerMetricsView;

public class Pickit2 implements IProgrammer {
	private File tempHexFile;
	private List<String> lastResult = new ArrayList<String>();
	
	public List<String> getLastResult() {
		return lastResult;
	}

	public File getTempHexFile() {
		return tempHexFile;
	}

	public int execute(IProject project, ProgrammerCommand command, String picID, String hexFile) {
		
		Process externalProcess;
		String programmerPath = ProgrammerPreferences.getProgrammerPath();
		String deviceDatPath = ProgrammerPreferences.getProgrammerDatFilePath();
		
		File file = new File(programmerPath);
		if (file!=null && !file.exists())
			return -1;
		file = new File(deviceDatPath);
		if (file!=null && !file.exists())
			return -1;		
		if (file!=null && !file.isDirectory())
			return -1;		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" -B" + deviceDatPath);
		sb.append(" -P");

		if (command != ProgrammerCommand.Connect) {
			if (picID == null)
				picID = JalPlugin.getDefault().getCurrentMCU();
	
			if (picID != null) {
				sb.append(picID);
				JalPlugin.getDefault().setCurrentMCU(picID);
			}
		}
		
		switch (command) {
		case Write: sb.append(" -E -JN -M -F" + hexFile); break;
		case Read:
			try {
				tempHexFile = File.createTempFile("pk-fileread", ".hex");
				tempHexFile.deleteOnExit();
			} catch (IOException e1) {
				e1.printStackTrace();
				return -1;
			}
			sb.append(" -GF" + tempHexFile); break;		    
		case Verify: sb.append(" -Y -F" + hexFile); break;
		case Erase: sb.append(" -E"); break;
		case BlankCheck: sb.append(" -C"); break;
		case Run: sb.append(" -W -R"); break;
		case Stop: break;
		case Connect: break;
		}
		
		String commandLine = programmerPath + " " + sb.toString();
		lastResult.clear();
				
		try {	    
		  externalProcess = Runtime.getRuntime().exec(commandLine);

		  final BufferedReader std = new BufferedReader ( new InputStreamReader( externalProcess.getInputStream() ) );
		  final BufferedReader stdErr = new BufferedReader ( new InputStreamReader( externalProcess.getErrorStream() ) );

		  Thread t1 = new Thread(new Runnable(){
			public void run() {
				String oneLine ;
				try {
					while ((oneLine = std.readLine())!= null) {
						//JalConsole.write(oneLine);
						lastResult.add(oneLine);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		  });
		  Thread t2 = new Thread(new Runnable(){
				public void run() {
					String oneLine ;
					try {
						while ((oneLine = stdErr.readLine())!= null) {
							//JalConsole.write(oneLine);
							lastResult.add(oneLine);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			  });
		  
		  t1.start();
		  t2.start();
		  while(t1.isAlive());
		  while(t2.isAlive());
		} catch ( Exception ex ) {
		  ex.printStackTrace();
		  return -1;
		}
		
		try {
			externalProcess.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
		
			Integer returnCode = externalProcess.exitValue();

			JalConsole.write("============================================================================================================================");
			JalConsole.write(commandLine);
			for(String line : lastResult)
				JalConsole.write(line);
			JalConsole.write("----------------------------------------------------------------------------------------------------------------------------");
			
			Pickit2ReturnCode result = new Pickit2ReturnCode(returnCode);
			JalConsole.write("Pickit 2 : " + result.toString());
			
			if (returnCode == 0) {
		    	for(String line : lastResult) {
		    		if (line.startsWith("Device Name = ")) {
		    			if (line.indexOf("<")<0) {
			    			line = line.substring(13).trim();
			    			JalPlugin.getDefault().setCurrentMCU(line);
			    			break;
		    			}
		    		}
		    		if (line.startsWith("Device Type: ")) {
		    			line = line.substring(12).trim();
		    			JalPlugin.getDefault().setCurrentMCU(line);
		    			break;
		    		}
		    		if (line.startsWith("Auto-Detect: Found part ")) {
		    			line = line.substring(24);
		    			int pos = line.indexOf(".");
		    			if (pos>0)
		    				line = line.substring(0, pos).trim();
		    			JalPlugin.getDefault().setCurrentMCU(line);
		    			break;
		    		}
		    	}				
			}
			else {
				showResult("Pickit2:"+result.toString());			
			}
				
			return returnCode;
		}catch (Exception e) {}
		return -1;
	}
	
	private void showResult(String result) {
		if (PlatformUI.getWorkbench().getWorkbenchWindows().length > 0) {
			if (PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages().length > 0) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages()[0];
				if (page != null) {
					try {
					IViewPart view = page.findView(CompilerMetricsView.COMPILERMETRICS_VIEW_ID);
					
					if (view != null && view instanceof CompilerMetricsView) {
						CompilerMetricsView metrics = (CompilerMetricsView) view;
						metrics.refreshCommandExecution(result);													
					}
					}catch (Exception ex){}
				}
			}
		}		
	}	
}
