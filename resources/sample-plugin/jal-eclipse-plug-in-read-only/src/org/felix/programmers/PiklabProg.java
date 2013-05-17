package org.felix.programmers;

/*
piklab prog

Descripción de Fernan:

http://sourceforge.net/apps/mediawiki/piklab/index.php?title=Command-line_Utility

Conectar         piklab-prog -c connect -p icd2 -t usb
Leer             piklab-prog -c read -p icd2 -t usb -d 18f4550 18f4550pwm.hex   (este comando me da error)
Verificar        piklab-prog -c verify -p icd2 -t usb -d 18f4550 18f4550pwm.hex 
Borrar           piklab-prog -c erase -p icd2 -t usb -d 18f4550
Grabar           piklab-prog -c program -p icd2 -t usb -d 18f4550 18f4550pwm.hex
Chequear borrado piklab-prog -c blank_check -p icd2 -t usb -d 18f4550
Run              piklab-prog -c run -p icd2 -t usb -d 18f4550
Stop             piklab-prog -c stop -p icd2 -t usb -d 18f4550

Despues esta la cuestion del firmware, pues el ICD2 escoge de un directorio que fichero le hace falta segun sea el micro a grabar.

piklab-prog -c program -p icd2 -t usb -d 16f877 --firmware-dir /home/fernan/datos/electronica/icd2  18f4550pwm.hex 

Con esta instruccion se carga automaticamente el fichero ICD que hace falta para el 16f877. 
Esto solo hace falta hacerlo cuando cambias de micro.
*/


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;
import org.felix.jal.eclipse.plugin.ui.preferences.ProgrammerPreferences;
import org.felix.jal.lang.JalElement;
import org.felix.jal.lang.JalParser;

public class PiklabProg implements IProgrammer {
	private File tempHexFile;
	private List<String> lastResult = new ArrayList<String>();

	@Override
	public List<String> getLastResult() {
		return lastResult;
	}

	@Override
	public File getTempHexFile() {
		return tempHexFile;
	}
	
	@Override
	public int execute(IProject project, ProgrammerCommand command, String picID, String hexFile) {
		Process externalProcess;
		String programmerPath = ProgrammerPreferences.getPiklabProgPath();
		String firmwareDirPath = ProgrammerPreferences.getPiklabProgFirmwareDir();

		File file = new File(programmerPath);
		if (file!=null && !file.exists())
			return -1;
		file = new File(firmwareDirPath);
		if (file!=null && !file.exists())
			return -1;		
		if (file!=null && !file.isDirectory())
			return -1;		
		
		StringBuilder sb = new StringBuilder();
		
		switch (command) {
		case Write: sb.append(" -c program"); break;
		case Read: sb.append(" -c read"); break;		    
		case Verify: sb.append(" -c verify"); break;
		case Erase: sb.append(" -c erase"); break;
		case BlankCheck: sb.append(" -c blank_check"); break;
		case Run: sb.append(" -c run"); break;
		case Stop: sb.append(" -c stop"); break;
		case Connect: sb.append(" -c connect"); break;
		}
		
		sb.append(" -p icd2 -t usb");				

		sb.append(" -d ");		
		
		String mainFile = JalParser.getMainFile(project);
		if (mainFile == null)
			return -1;
		
		file = new File(mainFile);
		if (file!=null && !file.exists())
			return -1;		

		Map<String, String> includesMap;
		try {
			includesMap = FileUtil.getIncludes(project, mainFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			return -1;
		}
		
		Map<String, Object> all = new TreeMap<String, Object>();
		JalParser.buildTree(file, includesMap, all, true, true);
		
		JalElement element = (JalElement) all.get("PICTYPE[]");
		if (element == null)
			return -1;
		
		file = new File(element.getFilename());
		if (file!=null && !file.exists())
			return -1;

		picID = JalParser.getDeviceType(element.getFilename());
		int pos = picID.indexOf("\"");
		if (pos < 0)
			return -1;
		picID = picID.substring(pos+1).trim();
		picID = picID.substring(0, picID.length()-1);
		
		sb.append(picID);
		
		sb.append(" --firmware-dir " + firmwareDirPath);

		switch (command) {
		case Write: sb.append(" " + hexFile); break;
		case Read:
			try {
				tempHexFile = File.createTempFile("piklabprog-fr", ".hex");
				tempHexFile.deleteOnExit();
			} catch (IOException e1) {
				e1.printStackTrace();
				return -1;
			}
			sb.append(" " + tempHexFile); break;		    
		case Verify: sb.append(" " + hexFile); break;
		case Erase: break;
		case BlankCheck: break;
		case Run: break;
		case Stop: break;
		case Connect: break;
		}
		
		String commandLine = programmerPath + " " + sb.toString();
		lastResult.clear();
		
		JalConsole.debug(commandLine);
		
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
			JalConsole.write("Piklab-prog : " + returnCode.toString());
			
			if (returnCode == 0) {
		    	for(String line : lastResult) {
		    		if (line.startsWith("Error: ")) {
		    			return -1;
		    		}
		    	}				
			}
			return returnCode;
		}catch (Exception e) {}
		
		return -1;
	}

}
