package org.facematic;

import java.io.PrintWriter;
import java.net.URL;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.facematic.facematic.editors.FmMvcEditor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.facematic.facematic"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static ILog logger;

	private static MessageConsoleStream consoleOutputStream;

	private static PrintWriter console;

	/**
	 * The constructor
	 */
	public Activator() {
		int m = 0;
		m++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;
		logger = Activator.getDefault().getLog();
		try {
			ServiceReference<?>[] services = Platform.getBundle(
					"org.eclipse.ui.console").getRegisteredServices();
			MessageConsole myConsole = new MessageConsole("Facematic Console",null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new MessageConsole[] { myConsole });
			ConsolePlugin.getDefault().getConsoleManager().showConsoleView(myConsole);
			final MessageConsoleStream stream = myConsole.newMessageStream();
			stream.setActivateOnWrite(true);
			consoleOutputStream = stream;
			console = new PrintWriter(consoleOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
			error ("Unable to prepare facematic console", e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		FmMvcEditor.shutDown();
		plugin = null;
		super.stop(context);
		consoleOutputStream.close();
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static URL getResourceURL(String path) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}
		URL url = BundleUtility.find(bundle, path);
		return url;
	}

	public static synchronized void info(String message, Throwable th) {
		logger.log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.INFO,
				message, null));

		if (th != null) {
			console.println("PLUGIN INFO:" + message);
			th.printStackTrace(console);
		}
		console.flush();
	}

	public static void info(String message) {
		info(message, null);
	}

	public static void error(String message, Throwable th) {
		logger.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.INFO,
				message, th));
		console.println("PLUGIN ERROR:  " + message);
		if (th != null)
			th.printStackTrace(console);
		console.flush();
	}

	public static MessageConsoleStream getConsoleOutputStream() {
		return consoleOutputStream;
	}

}
