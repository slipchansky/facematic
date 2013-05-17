package org.felix.jal.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.felix.jal.eclipse.plugin.ui.console.JalConsole;

public final class JalCompiler {
	private JalCompiler() {
	}

	public static JalCompileResult compile(String compilerPath, String mainFile, 
			String libPath, String options) {
		
		final StringBuffer res = new StringBuffer();
		final List<String> lastResult = new ArrayList<String>();
		final ArrayList<JalErrors> errors = new ArrayList<JalErrors>();
		String commandLine = compilerPath +" -s "+libPath;
		
		if ((options!= null) && (!"".equals(options)))
			commandLine += " " + options;		
				
		commandLine += " " + mainFile;
		Process externalProcess;
		JalConsole.write(commandLine);
		
		try {	    
		  externalProcess = Runtime.getRuntime().exec ( commandLine );

		  final BufferedReader std = new BufferedReader ( new InputStreamReader( externalProcess.getInputStream() ) );
		  final BufferedReader stdErr = new BufferedReader ( new InputStreamReader( externalProcess.getErrorStream() ) );

		  Thread t1 = new Thread(new Runnable(){
			public void run() {
				String oneLine ;
				try {
					Pattern pattern = Pattern.compile("(.*):(\\d+):(.*)$");
					while ((oneLine = std.readLine())!= null) {
						JalConsole.write(oneLine);
						lastResult.add(oneLine);						
					    Matcher matcher = pattern.matcher(oneLine);
					    if(matcher.matches()) {
							JalErrors err = new JalErrors();
							String file = matcher.group(1);
							String line = matcher.group(2);
							String msg = matcher.group(3);
							err.setFile(file);
							err.setLine(Integer.parseInt(line));
							err.setMessage(msg);
							errors.add(err);
					    }
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
							res.append(oneLine+"\n");
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
		} 
		
		JalCompileResult result = new JalCompileResult();
		result.setResult(res.toString());
		result.setErrors(errors.toArray(new JalErrors[errors.size()]));

		/*
		jal 2.4o (compiled May  8 2011)
		generating p-code
		1575 tokens, 119718 chars; 3041 lines; 8 files
		generating PIC code pass 1
		generating PIC code pass 2
		writing result
		Code area: 298 of 32768 used (bytes)
		Data area: 21 of 1408 used
		Software stack available: 1387 bytes
		Hardware stack depth 2 of 31
		0 errors, 0 warnings 	
		 */
		
		try {
			int value;
			int pos;
			for(String line : lastResult) {
				if (line.startsWith("jal ")) {
					result.setCompiler(line);
					continue;
				}
				
				if (line.indexOf("tokens")>0) {
					pos = line.indexOf("tokens");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setTokens(value);
					
					pos = line.indexOf(",");
					line = line.substring(pos+1);
					pos = line.indexOf("chars");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setChars(value);
	
					pos = line.indexOf(";");
					line = line.substring(pos+1);
					pos = line.indexOf("lines");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setLines(value);
	
					pos = line.indexOf(";");
					line = line.substring(pos+1);
					pos = line.indexOf("files");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setFiles(value);				
					continue;
				}
				if (line.startsWith("Code area")) {
					pos = line.indexOf(":");
					line = line.substring(pos+1);
					pos = line.indexOf("of");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setCodeAreaBytesUsed(value);
	
					pos = line.indexOf("of");
					line = line.substring(pos+3);
					pos = line.indexOf("used");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setCodeAreaBytesTotal(value);
					continue;
				}
				if (line.startsWith("Data area")) {
					pos = line.indexOf(":");
					line = line.substring(pos+1);
					pos = line.indexOf("of");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setDataAreaBytesUsed(value);
	
					pos = line.indexOf("of");
					line = line.substring(pos+3);
					pos = line.indexOf("used");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setDataAreaBytesTotal(value);				
					continue;
				}
				if (line.startsWith("Software stack available")) {
					pos = line.indexOf(":");
					line = line.substring(pos+1);
					pos = line.indexOf("bytes");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setSoftStackAvailable(value);
					continue;
				}
				if (line.startsWith("Hardware stack depth")) {
					line = line.substring(21);
					pos = line.indexOf("of");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setHardStackDepthUsed(value);
	
					pos = line.indexOf("of");
					line = line.substring(pos+3);
					value = Integer.parseInt(line.trim());
					result.setHardStackDepthTotal(value);	
					continue;
				}
				if (line.indexOf("errors")>0 && line.indexOf("warnings")>0) {
					pos = line.indexOf("errors");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setNumErrors(value);
					
					pos = line.indexOf(",");
					line = line.substring(pos+1);
					pos = line.indexOf("warnings");
					value = Integer.parseInt(line.substring(0, pos-1).trim());
					result.setNumWarnings(value);
					continue;
				}				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}


}