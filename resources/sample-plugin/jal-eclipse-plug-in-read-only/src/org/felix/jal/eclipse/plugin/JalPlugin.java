package org.felix.jal.eclipse.plugin;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class JalPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.felix.jal.eclipse.plugin"; //$NON-NLS-1$
	public static final String	DEFAULT_CONSOLE	= "JAL Console";

	// The shared instance
	private static JalPlugin plugin;
	
	private String currentMCU;
	
	public String getCurrentMCU() {
		return currentMCU;
	}

	public void setCurrentMCU(String currentMCU) {
		this.currentMCU = currentMCU;
	}

	/**
	 * The constructor
	 */
	public JalPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JalPlugin getDefault() {
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
	
	/**
	 * Log the given status and print it to the err Stream if debugging is enabled.
	 * 
	 * @param status
	 */
	public void log(IStatus status) {
		ILog log = getLog();
		if (status.getSeverity() >= Status.WARNING)
			log.log(status);

		if (isDebugging()) {
			System.err.print(PLUGIN_ID + ": " + status.getMessage());
			if (status.getCode() != 0) 
				System.err.print("(" + status.getCode() + ")");
			
			System.out.println("");
			if (status.getException() != null) 
				status.getException().printStackTrace();
		}
	}
	
	/**
	 * Gets the default console for this plugin.
	 * <p>
	 * This console is used for logging internal information, like the output of the external tools
	 * called internally (not by user interaction). This Console is only for background information
	 * and debugging and should never be actively brought to the front.
	 * </p>
	 * 
	 * @return The default <code>MessageConsole</code>
	 */
	public MessageConsole getDefaultConsole() {
		return getConsole(DEFAULT_CONSOLE);
	}

	/**
	 * Gets the console with the given name.
	 * <p>
	 * This is a convenience method to get a console with the given name.
	 * </p>
	 * <p>
	 * If the console already exists, a reference is returned, otherwise a new
	 * <code>MessageConsole</code> with the given name is created, added to the ConsoleManager,
	 * and returned.
	 * </p>
	 * 
	 * @param name
	 *            The name of the Console
	 * @return A <code>MessageConsole</code> with the given name
	 */
	public MessageConsole getConsole(String name) {
		// Get a list of all known Consoles from the Global Console Manager and
		// see if a Console with the given name already exists.
		IConsoleManager conman = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] allconsoles = conman.getConsoles();
		for (IConsole console : allconsoles) {
			if (console.getName().equals(name)) {
				return (MessageConsole) console;
			}
		}
		// Console not found - create a new one
		MessageConsole newconsole = new MessageConsole(name, null);
		conman.addConsoles(new IConsole[] { newconsole });
		return newconsole;
	}	
}
