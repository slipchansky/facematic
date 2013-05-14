package org.facematic.fmweb.plugin;




import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.servlet.FacematicServlet;
import org.facematic.core.ui.FacematicUI;


/**
 * 
 * @author papa
 *
 */
public class FmInternalServletForPluginPurposes extends FacematicServlet {
	
	static class OutputStreamProxy extends OutputStream {
		private Object osInstance;
		private Method write;
		
		public OutputStreamProxy (Object instance) throws NoSuchMethodException, SecurityException {
			this.osInstance = instance;
			write = instance.getClass().getMethod("write", Integer.TYPE);
		}

		@Override
		public void write(int b) throws IOException {
			try {
				write.invoke(osInstance, b);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	

	@Override
	public Class<? extends FacematicUI> getUiClass() {
		return FmAppUIforPluginPurposes.class; 
	}
	
	public static void setPluginOutputStream (Object pluginOutputStream) {
		try {
			LoggerFactory.setPluginConsoleOutputStream(new OutputStreamProxy (pluginOutputStream));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
 	}
	

}
