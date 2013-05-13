package org.facematic;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.facematic.facematic.editors.FmMvcEditor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.facematic.facematic"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static ILog logger;	
	
	
	/**
	 * The constructor
	 */
	public Activator() {
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		logger = Activator.getDefault().getLog();		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		FmMvcEditor.shutDown ();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static URL getResourceURL (String path) {
		 Bundle bundle = Platform.getBundle(PLUGIN_ID);
	        if (!BundleUtility.isReady(bundle)) {
				return null;
			}
	        URL url = BundleUtility.find(bundle, path);
	        return url;
	}

	public static void info (String message) {
		logger.log (new Status(Status.INFO, Activator.PLUGIN_ID, Status.INFO, message, null));
	}
	
	public static void error (String message, Throwable exception) {
		logger.log (new Status(Status.ERROR, Activator.PLUGIN_ID, Status.INFO, message, exception));
	}
	
	
}
