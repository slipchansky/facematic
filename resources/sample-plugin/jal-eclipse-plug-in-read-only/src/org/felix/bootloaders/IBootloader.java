package org.felix.bootloaders;

import java.io.File;

public interface IBootloader {
	public File getTempHexFile();
	public int execute(BootloaderCommand command, String hexFile);
}
