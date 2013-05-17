package org.felix.jal.eclipse.plugin.ui.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public final class JalConsole {

	private JalConsole() {
	}

	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	
	public static void write(String message) {
//		System.out.println("CONSOLE>"+message);
		MessageConsole console = getConsole();
		MessageConsoleStream stream = console.newMessageStream();
		stream.println(message);
	}
	
	public static void debug(String message) {
		MessageConsole console = getConsole();
		MessageConsoleStream stream = console.newMessageStream();
		stream.println("DEBUG>"+message);
	}
	
	public static void write(Exception e) {
		System.out.println("CONSOLE EXCEPTION :>");
		e.printStackTrace();
		MessageConsole console = getConsole();
		MessageConsoleStream stream = console.newMessageStream();
		stream.println("Exception occured : "+e.getClass().getName()+" -"+e.getMessage());
	}
	
	
	private static MessageConsole getConsole() {
		MessageConsole console = findConsole("JAL Console");
		if (console==null ){	
			console = new MessageConsole("JAL Console", null);
			console.activate();
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
		}
		return console;
	}
	

}