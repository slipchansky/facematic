package org.felix.jal.eclipse.plugin.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.felix.bootloaders.BootloaderCommand;
import org.felix.bootloaders.BootloaderFactory;
import org.felix.bootloaders.IBootloader;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;

public class BootloaderVerifyAction extends BootloaderActionBase {
	
	@Override
	public void run(IAction action) {
		super.run(action);
		
		if (activeProject != null) {
			IFile[] hexFiles = null;
			
			try {
				hexFiles = FileUtil.getHexFile(activeProject.getProject());
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			if (hexFiles != null) {
				if (hexFiles.length > 1) {
					MessageDialog.openError(
					window.getShell(),
					"JAL Eclipse Plug-in", "El proyecto no puede tener más de un archivo .hex");
					return;
				}
				if (hexFiles.length == 0) {
					MessageDialog.openError(
					window.getShell(),
					"JAL Eclipse Plug-in", "El proyecto no posee un archivo .hex");
					return;
				}
				
				String hexFile = hexFiles[0].getLocation().toOSString();
			
				IBootloader bootloader = BootloaderFactory.create();
				bootloader.execute(BootloaderCommand.Verify, hexFile);
				
			}				
		}	
	}
}
