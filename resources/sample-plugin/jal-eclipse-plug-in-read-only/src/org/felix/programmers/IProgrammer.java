package org.felix.programmers;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;

public interface IProgrammer {

	public List<String> getLastResult();
	public File getTempHexFile();
	public int execute(IProject project, ProgrammerCommand command, String picID, String hexFile);
}
