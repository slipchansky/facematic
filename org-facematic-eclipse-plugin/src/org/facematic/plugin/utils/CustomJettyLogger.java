package org.facematic.plugin.utils;

import org.eclipse.jetty.util.log.Logger;
import org.facematic.Activator;

public class CustomJettyLogger implements org.eclipse.jetty.util.log.Logger {
	
	final static CustomJettyLogger loggerInstance = new CustomJettyLogger ();

	@Override
	public void debug(Throwable th) {
		Activator.info("DEBUG:", th);
		
	}
	
	String str (Object... arg) {
		String result = "\n";
		for (Object o : arg) {
			result+= (""+o)+"\n";
		}
		return result;
	}

	@Override
	public void debug(String message, Object... arg) {
		Activator.info("DEBUG:"+message+str (arg));
		
	}

	@Override
	public void debug(String msg, Throwable th) {
		Activator.info("DEBUG:"+msg, th);
		
	}

	@Override
	public Logger getLogger(String arg0) {
		return loggerInstance;
	}

	@Override
	public String getName() {
		return "facematic";
	}

	@Override
	public void ignore(Throwable th) {
		Activator.info("IGNORE:", th);
	}

	@Override
	public void info(Throwable th) {
		Activator.info("INFO:", th);
	}

	@Override
	public void info(String msg, Object... arg1) {
		Activator.info("INFO:"+msg+str (arg1));
	}

	@Override
	public void info(String msg, Throwable th) {
		Activator.info("INFO:"+msg, th);
		
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void setDebugEnabled(boolean arg0) {
	}

	@Override
	public void warn(Throwable arg0) {
		Activator.info("WARN:", arg0);
	}

	@Override
	public void warn(String arg0, Object... arg1) {
		Activator.info("WARN:"+ arg0+str (arg1));
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		Activator.info("WARN:"+ arg0, arg1);
	}

}
