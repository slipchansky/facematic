package org.felix.jal.eclipse.plugin.ui.editors.hyperlink;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;
import org.felix.jal.eclipse.plugin.ui.editors.JalEditor;
import org.felix.jal.eclipse.plugin.ui.preferences.JalEditorPreferences;
import org.felix.jal.lang.DatasheetLink;
import org.felix.jal.lang.FileLink;
import org.felix.jal.lang.JalElement;
import org.felix.jal.lang.JalParser;


public class JalHyperlink implements IHyperlink {

	private final IRegion fRegion;
	private JalEditor fEditor;

	public JalHyperlink(IRegion region, JalEditor editor) {
		Assert.isNotNull(region);
		fRegion = region;
		fEditor = editor;
	}

	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	public String getHyperlinkText() {
		return null;
	}

	public String getTypeLabel() {
		return null;
	}

	/**
	 * Try to find a definition and open it.
	 */
	public void open() {
		IDocument doc = fEditor.getDocument();
		String selectedWord = "";
		String lineStart = "";
		String lineEnd = "";
		try {
			selectedWord = doc.get(fRegion.getOffset(), fRegion.getLength());
			int offset = fRegion.getOffset() - 1;
			while ((offset >= 0) && (!"\n".equals(doc.get(offset, 1)))) {
				lineStart = doc.get(offset, 1) + lineStart;
				offset--;
			}
			offset = fRegion.getOffset() + fRegion.getLength();
			while ((offset < doc.getLength()) && (!"\n".equals(doc.get(offset, 1)) && (!"\r".equals(doc.get(offset, 1))))) {
				lineEnd = lineEnd + doc.get(offset, 1);
				offset++;
			}
		} catch (BadLocationException e) {
			JalConsole.write(e);
		}
		
		IFileEditorInput input = (IFileEditorInput) fEditor.getEditorInput();
		try {
			if (lineStart.trim().toLowerCase().startsWith(";@file:")) {
				int pos = lineStart.indexOf(":");
				if (pos == lineStart.length())
					selectedWord =  selectedWord + lineEnd;
				else
					selectedWord = lineStart.substring(pos+1).trim() + selectedWord + lineEnd;
			}
						
			if (lineStart.trim().toLowerCase().startsWith("const byte datasheet[]")) {
				selectedWord = "ds:" + selectedWord;
			}
			
			if ("include".equalsIgnoreCase(lineStart.trim())){
				IProject prj = input.getFile().getProject();
				IFile[] files = FileUtil.findAllReferencedFiles(prj);
				for (int i = 0;i<files.length;i++) {
					if (files[i].getName().equalsIgnoreCase(selectedWord+ ".jal")) {
						FileUtil.openJalFile(files[i]);
						break;
					}
				}
			}
			else {
				selectedWord = selectedWord.toLowerCase();
				
				// if it's not an include, try to find method declaration

				Map<String, Object> all = new TreeMap<String, Object>();
				
				// find avaiable func / proc / vars
				//File file = new File(input.getFile().getLocation().toOSString());

				File file;
				
				String mainFile = JalParser.getMainFile(input.getFile().getProject());
				if (mainFile != null)
					file = new File(mainFile);
				else {
					mainFile = input.getFile().getLocation().toOSString();
					file = new File(mainFile);
				}

				Map<String, String> includesMap = FileUtil.getIncludes(input.getFile().getProject(), mainFile);
				
				JalParser.buildTree(file, includesMap, all, false, false);
				
				JalElement foundElement = (JalElement) all.get(selectedWord);
				if (foundElement == null)
					foundElement = (JalElement) all.get(selectedWord + "'get");
					if (foundElement == null)
						foundElement = (JalElement) all.get(selectedWord + "'put");
				
				if (foundElement != null) {
					if (foundElement instanceof FileLink) {
						FileLink link = (FileLink) foundElement;

						String commandLine;
						if (link instanceof DatasheetLink) {
							for(int i=0; i<7; i++) {
								String datasheetsPath = JalEditorPreferences.getDatasheetFolder();
								
								if (datasheetsPath != null && new File(datasheetsPath).exists()) {
									String fileName = datasheetsPath + File.separator + link.getTypedName() + (char)(65+i) + ".pdf";

									if (new File(fileName).exists()) {
										String command = JalEditorPreferences.getCommandFromExtension("pdf");
										
										if (command != null) {
											commandLine =  command + " " + fileName;
											Runtime.getRuntime().exec(commandLine);
											break;
										}
									}
								}
							}
						}
						else {
							String linkFilesPath = JalEditorPreferences.getLinkFilesFolder();
							if (linkFilesPath != null && new File(linkFilesPath).exists()) {
								String fileName = linkFilesPath + File.separator + link.getTypedName();
								
								if (new File(fileName).exists()) {
									String extension = null;
									int pos = link.getTypedName().indexOf(".");
									
									if (pos > 0)
										extension = link.getTypedName().substring(pos+1).toLowerCase();
									
									if (extension != null) {
										String command = JalEditorPreferences.getCommandFromExtension(extension);
			
										if (command != null) {
											commandLine =  command + " " + fileName;
											Runtime.getRuntime().exec(commandLine);
										}
									}
								}
							}
						}							
					}
					else {
						String filename = foundElement.getFilename();
						if (filename != null) {
							JalEditor edit = FileUtil.openJalFile(input.getFile().getProject(),filename);
							FileUtil.selectText(edit,selectedWord.toUpperCase());
						}
					}
				}
			}
		} catch (Exception e1) {
			JalConsole.write(e1);
		}

	}
}
