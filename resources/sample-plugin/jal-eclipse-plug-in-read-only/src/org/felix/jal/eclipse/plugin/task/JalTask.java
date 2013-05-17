package org.felix.jal.eclipse.plugin.task;

public class JalTask {
	public String message;
	public int line;
	public int priority;
	 
	public JalTask(String message, int line, int priority)
	{
		this.message = message;
		this.line = line;
		this.priority = priority;
	}
}
