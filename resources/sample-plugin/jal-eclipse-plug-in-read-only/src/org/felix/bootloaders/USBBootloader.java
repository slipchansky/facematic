package org.felix.bootloaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.preferences.BootloaderPreferences;
import org.felix.jal.eclipse.plugin.ui.views.CompilerMetricsView;

public class USBBootloader implements IBootloader {
	private File tempHexFile;
	private List<String> lastResult = new ArrayList<String>();
	
	public List<String> getLastResult() {
		return lastResult;
	}

	public File getTempHexFile() {
		return tempHexFile;
	}

	@Override
	public int execute(BootloaderCommand command, String hexFile) {
		Process externalProcess;
		String bootloaderPath = BootloaderPreferences.getBootloaderPath();
		
		File file = new File(bootloaderPath);
		if (file!=null && !file.exists())
			return -1;
		
		StringBuilder sb = new StringBuilder();
		
		switch (command) {
		case Write: sb.append(" --program " + hexFile); break;
		case Read:
			try {
				tempHexFile = File.createTempFile("fsusb-fileread", ".hex");
				tempHexFile.deleteOnExit();
			} catch (IOException e1) {
				e1.printStackTrace();
				return -1;
			}
			sb.append(" --read " + tempHexFile); break;		    
		case Verify: sb.append(" --verify " + hexFile); break;
		}
		
		String commandLine = bootloaderPath + " " + sb.toString();
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
			//JalConsole.write("Bootloader : " + new ...
			
			String result = null;
			for(String line : lastResult) {
				if (line.startsWith("Fatal ")) {
					result = line;
					break;
				}	
			}
			if (result==null)
				result = lastResult.get(lastResult.size()-1);
			showResult("FSUSB: "+result);			
			
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
