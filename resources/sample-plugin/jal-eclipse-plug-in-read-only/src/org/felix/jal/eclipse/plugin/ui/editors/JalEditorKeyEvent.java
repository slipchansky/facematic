package org.felix.jal.eclipse.plugin.ui.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.ui.ide.ResourceUtil;
import org.felix.jal.compiler.JalCompileResult;
import org.felix.jal.eclipse.plugin.builder.JalBuilder;
import org.felix.jal.lang.JalParser;


public class JalEditorKeyEvent implements KeyListener {
	private JalEditor editor;

	public JalEditorKeyEvent(JalEditor editor) {
		super();
		this.editor = editor;
		
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyPressed(KeyEvent evt) {
		 
		/*
		 * Manually compile file using F9 key
		 * works even if the project as no JAL nature...
		 */
		if (SWT.F9 == evt.keyCode) {
			IResource resource = ResourceUtil.getResource(editor.getEditorInput());
			if ("jal".equals(resource.getFileExtension())) {
			    editor.doSave(null);
			    
				String mainFile = JalParser.getMainFile(resource.getProject());
				
				if (mainFile!=null) {
					JalCompileResult result = JalBuilder.compileResource(resource.getProject(), mainFile);
					JalBuilder.updateMarkers(resource, result);
					try {
						ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
								IResource.DEPTH_INFINITE, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (' ' == evt.character) {
			/* Auto complete structure */
			JalCompleteStructureProcessor.codeAssist((StyledText) evt.getSource());
		}
	}

}
