package org.facematic.core.logging;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggerRepository;

public class LoggerFactory {
	static Logger logger = Logger.getLogger(LoggerFactory.class);
	static Set<Logger> existingLoggers = new HashSet<Logger> ();  
	
	private static OutputStream pluginConsoleOutputStream;
	
	public static Logger getLogger (Class clazz) {
		Logger logger = Logger.getLogger(clazz);
		implementOutputStream(logger);
		existingLoggers.add(logger);
		return logger;
	}

	public static void setPluginConsoleOutputStream(OutputStream pluginConsoleOutputStream) {
		LoggerFactory.pluginConsoleOutputStream = pluginConsoleOutputStream;
		implementPluginOutputStream ();
	}
	
	private static void implementPluginOutputStream () {
		if (pluginConsoleOutputStream == null) {
			return;
		}
//		implementOutputStream(Logger.getRootLogger());
		for (Logger logger : existingLoggers) {
			implementOutputStream(logger);
		}
		
	}

	private static void implementOutputStream(Logger logger) {
		if (pluginConsoleOutputStream == null) {
			return;
		}
		Appender appender = logger.getAppender("streamOutputAppender");
		if (appender == null) {
			appender = new WriterAppender(new SimpleLayout(), pluginConsoleOutputStream);
			appender.setName("streamOutputAppender");
			logger.addAppender(appender);
		} 
	}
}
