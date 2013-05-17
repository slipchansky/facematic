package org.felix.jal.eclipse.plugin.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public final class FileUtil {
	private FileUtil() {
	}

	/**
	 * find all referenced files (ie all files that belongs to this project, or
	 * to a referenced project)
	 */
	public static IFile[] findAllReferencedFiles(IProject prj) throws Exception {

		ArrayList<IFile> result = new ArrayList<IFile>();
		String dir;

		dir = prj.getLocation().toOSString();
		File fdir = new File(dir);
		List<File> fileList = getFileListing(fdir);
		File[] files = (File[]) fileList.toArray(new File[fileList.size()]);
		
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().endsWith(".jal")) {
					result.add(prj.getFile(files[i].getAbsolutePath().substring(prj.getLocation().toOSString().length())));
				}
			}
		}
		IProject[] refPrj = prj.getReferencedProjects();
		if (refPrj != null) {
			for (int p = 0; p < refPrj.length; p++) {
				if (refPrj[p].getLocation() != null) {
					prj = refPrj[p];
					dir = prj.getLocation().toOSString();
					fdir = new File(dir);
					fileList = getFileListing(fdir);
					files = (File[]) fileList.toArray(new File[fileList.size()]);
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							if (files[i].getName().endsWith(".jal")) {
								result.add(prj.getFile(files[i].getAbsolutePath().substring(prj.getLocation().toOSString().length())));
							}
						}
					}
				}				
			}
		}
		return result.toArray(new IFile[result.size()]);
	}

	public static IFile[] getHexFile(IProject prj) throws Exception {

		ArrayList<IFile> result = new ArrayList<IFile>();
		String dir;

		dir = prj.getLocation().toOSString();
		File fdir = new File(dir);
		List<File> fileList = getFileListing(fdir);
		File[] files = (File[]) fileList.toArray(new File[fileList.size()]);
		
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().toLowerCase().endsWith(".hex")) {
					result.add(prj.getFile(files[i].getAbsolutePath().substring(prj.getLocation().toOSString().length())));
				}
			}
		}
		return result.toArray(new IFile[result.size()]);
	}
	
	
	public static List<File> getFileListing(File aStartingDir)
			throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = new ArrayList<File>();

		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (Iterator<File> it = filesDirs.iterator(); it.hasNext();) {
			File file = it.next();
			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<File> deeperList = getFileListing(file);
				result.addAll(deeperList);
			}
		}
		Collections.sort(result);
		return result;
	}

	private static void validateDirectory(File aDirectory)
			throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: "
					+ aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: "
					+ aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: "
					+ aDirectory);
		}
	}

	/**
	 * Adapt a filename to an IFile
	 * 
	 */
	public static IFile toIFile(IProject project, String file) throws Exception {

		// current project
		String prjPath = toSlashPath(project.getLocation().toOSString());
		file = toSlashPath(file);
		if (toSlashPath(file).startsWith(prjPath)) {
			String filename = file.substring(prjPath.length());
			return project.getFile(filename);
		}

		// lib path
		IProject[] refPrj = project.getReferencedProjects();
		if (refPrj != null) {
			for (int i = 0; i < refPrj.length; i++) {
				IProject prj = refPrj[i];
				prjPath = toSlashPath(prj.getLocation().toOSString());
				if (file.startsWith(prjPath)) {
					String filename = file.substring(prjPath.length());
					return prj.getFile(filename);
				}
			}
		}
		return null;
	}

	private static String toSlashPath(String file) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < file.length(); i++) {
			if (file.charAt(i) == '\\') {
				buf.append("/");
			} else {
				buf.append(file.charAt(i));
			}
		}
		return buf.toString();

	}
	
	/**
	 * Get all include for this editor.
	 * useful for auto-completion of include
	 */
	public static Map<String, String> getIncludes(IProject project, String fileName) throws Exception{
		Map<String, String> includes = new TreeMap<String, String>();
		
		IFile[] files = FileUtil.findAllReferencedFiles(project);
		for (int i = 0;i<files.length;i++) {			
			if(! fileName.equals(files[i].getLocation().toOSString())) {
				String includeName = files[i].getName();
				includeName = includeName.substring(0,includeName.lastIndexOf(files[i].getFileExtension())-1);
				String includePath = files[i].getLocation().toOSString();
				includes.put(includeName.toLowerCase(),includePath);
			}
		}
		return includes;
	}

	
	
	public static JalEditor openJalFile(IProject p, String filename)
			throws Exception {
		return openJalFile(toIFile(p, filename));
	}
	
	public static JalEditor openJalFile(IFile fileToBeOpened)
	throws Exception {
		if (fileToBeOpened.exists()) {
			// OPen file
			IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IEditorPart part = page.openEditor(editorInput, "org.felix.jal.eclipse.plugin.ui.editors.JalEditor");
			return (JalEditor)part;
			
		} else
			return null;
	}
	
	public static void selectText(JalEditor editor, String search) {
		String t1 = editor.getTextWidget().getText().toUpperCase();
		StringBuffer buf = new StringBuffer();
		boolean comment = false;
		/*
		 * Replace comment by blank chars
		 */
		for (int i = 0; i < t1.length(); i++) {
			char cChar = t1.charAt(i);
			char nChar = ' ';
			try {
				nChar = t1.charAt(i + 1);
			} catch (Exception e) {
			}
			if (cChar == '\n') {
				comment = false;
			}
			if (cChar == ';') {
				comment = true;
			}
			if ((cChar == '-') && (nChar == '-')) {
				comment = true;
			}
			if (comment) {
				buf.append(" ");
			} else {
				buf.append(cChar);
			}
		}
		String t = buf.toString();
				
		int start = 0;
		start = t.indexOf("INCLUDE " + search.toUpperCase());
		if (start != -1) {
			start += "INCLUDE ".length();
			int end = start + search.length();
			editor.getTextWidget().setSelection(start, end);
		} else {
			String s = search.toUpperCase();
			if (s.endsWith("]"))
				s = s.substring(0, s.length()-2)+".*\\[)";  //se trata de un tipo array
			else
				s = s + "\\s)";

			//búsqueda básica de la variable, si está repetida en su nombre muestra
			//la primer ocurrencia
			Pattern pattern = Pattern.compile("(VAR |CONST |ALIAS )[^\\[\\=\\:\\r\\n]*(\\b" + s);
			Matcher matcher = pattern.matcher(t);
			if (matcher.find())	
				editor.getTextWidget().setSelection(matcher.start(), matcher.end()-1);			
			else {
				//(procedure )[^\(\r\n]*(\bsearch[^\w]*\()
				
				pattern = Pattern.compile("(PROCEDURE )[^\\(\\r\\n]*(\\b" + search.toUpperCase() + "[^\\w]*\\()");
				matcher = pattern.matcher(t);
				if (matcher.find())
					editor.getTextWidget().setSelection(matcher.start(), matcher.end()-1);
				else {
					pattern = Pattern.compile("(PROCEDURE )[^\\(\\r\\n]*(\\b" + search.toUpperCase() + "'PUT[^\\w]*\\()");
					matcher = pattern.matcher(t);
					if (matcher.find())
						editor.getTextWidget().setSelection(matcher.start(), matcher.end()-1);
					else {
						pattern = Pattern.compile("(FUNCTION )[^\\(\\r\\n]*(\\b" + search.toUpperCase() + "[^\\w]*\\()");
						matcher = pattern.matcher(t);
						if (matcher.find()) 
							editor.getTextWidget().setSelection(matcher.start(), matcher.end()-1);
						else {
							pattern = Pattern.compile("(FUNCTION )[^\\(\\r\\n]*(\\b" + search.toUpperCase() + "'GET[^\\w]*\\()");
							matcher = pattern.matcher(t);
							if (matcher.find()) 
								editor.getTextWidget().setSelection(matcher.start(), matcher.end()-1);
						}
					}						
				}
			}
		}
	}
}
