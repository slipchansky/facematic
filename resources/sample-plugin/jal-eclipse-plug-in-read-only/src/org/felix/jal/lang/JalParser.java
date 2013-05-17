package org.felix.jal.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;

public final class JalParser {
	private JalParser() {}
	/**
	 * Build a structure representing the file
	 * this is useful to file an outline view
	 * @param text
	 * @return
	 */
	public static JalContainer getFileStructure(String text) {
		JalContainer root = new JalContainer("root");
		JalContainer inc = new JalContainer("includes");
		JalContainer methods = new JalContainer("methods");
		JalContainer vars = new JalContainer("vars");
		JalContainer consts = new JalContainer("consts");
		JalContainer aliases = new JalContainer("aliases");
		JalContainer links = new JalContainer("links");
		root.addElement(inc);
		root.addElement(methods);
		root.addElement(consts);
		root.addElement(vars);
		root.addElement(aliases);
		root.addElement(links);
		
		String[]lines = text.split("\n");
		for (int i = 0;i<lines.length;i++) {
			String line = lines[i];
			line = line.trim();
			String upperLine = line.toUpperCase();
			if (upperLine.startsWith("INCLUDE ")) {
				String name = line.substring("INLCUDE ".length());
				name = prepareName(name);
				JalInclude el = new JalInclude(name.toLowerCase(), name);
				inc.addElement(el);
			} else if (upperLine.startsWith("PROCEDURE ")) {
				String name = line.substring("PROCEDURE ".length());
				name = prepareName(name);
				
				JalMethod el = new JalMethod(name.toLowerCase(), name);
				try {el.setParams(line.substring(line.indexOf('(')+1,line.indexOf(')')));}catch (Exception e){}
				methods.addElement(el);
			} else if (upperLine.startsWith("FUNCTION ")) {
				String name = line.substring("FUNCTION ".length());
				name = prepareName(name);

				JalMethod el = new JalMethod(name.toLowerCase(), name);
				try {el.setParams(line.substring(line.indexOf('(')+1,line.indexOf(')')));}catch (Exception e){}
				methods.addElement(el);
				
			// Las variables/constantes/alias los busca sin tener en cuenta el ámbito 
			} else if (upperLine.startsWith("VAR ")) {
				String name = line.substring("VAR ".length());
				name = prepareVarName(name);
				JalVariable var = new JalVariable(name.toLowerCase(), name);
				vars.addElement(var);
			} else if (upperLine.startsWith("CONST ")) {
				String name = line.substring("CONST ".length());
				name = prepareVarName(name);
				JalConst var = new JalConst(name.toLowerCase(), name);				
				consts.addElement(var);
				
				if (name.toUpperCase().equals("DATASHEET[]")) {
					int pos = line.indexOf("\""); 
					if (pos > 0) {
						name = line.substring(pos+1, line.length()-1);
						DatasheetLink link = new DatasheetLink("ds:"+name.toLowerCase(), name);
						links.addElement(link);
					}
				}
			} else if (upperLine.startsWith("ALIAS ")) {
				String name = line.substring("ALIAS ".length());
				name = prepareVarName(name);
				JalAlias var = new JalAlias(name.toLowerCase(), name);
				aliases.addElement(var);
			}
			if (upperLine.startsWith(";@FILE:")) {
				int pos = line.indexOf(":");
				String name = line.substring(pos+1).trim();
				FileLink link = new FileLink(name.toLowerCase(), name);
				links.addElement(link);
			}
		}
		return root;
	}
	
	private static String prepareName(String name) {
		
		name = name.trim();
		
		int pos = name.indexOf('(');
		if (pos >= 0) {
			name = name.substring(0,pos);
		}

		pos = name.indexOf("--");
		if (pos >= 0) {
			name = name.substring(0,pos);
		}

		pos = name.indexOf(';');
		if (pos >= 0) {
			name = name.substring(0,pos);
		}
		
		return name.trim();
	}
	
	private static String prepareVarName(String name) {

		int pos;
				
		do {
			name = name.trim();
			pos = -1;
			for(int i=0; i<JalConstant.TYPES.length; i++) {				
				if (name.startsWith(JalConstant.TYPES[i]+" ")) {
					pos = 1;
					name = name.substring(JalConstant.TYPES[i].length()+1);
					break;
				}
			}
		} while (pos >= 0);
		
		name = name.trim();
		
		pos = name.indexOf(" shared ");
		if (pos >= 0) {
			name = name.substring(0, pos);
		}

		pos = name.indexOf(" at ");
		if (pos >= 0) {
			name = name.substring(0,pos);
		}

		pos = name.indexOf(" is ");
		if (pos >= 0) {
			name = name.substring(0,pos);
		}

		pos = name.indexOf('=');
		if (pos >= 0) {
			name = name.substring(0,pos);
		}

		pos = name.indexOf('[');
		if (pos >= 0) {
			name = name.substring(0,pos) + "[]";			
		}

		pos = name.indexOf(';');
		if (pos >= 0) {
			name = name.substring(0,pos);			
		}
		
		pos = name.indexOf('*');
		if (pos >= 0) {
			pos = name.indexOf(' ');
			name = name.substring(pos);
		}
				
		return name.trim();	
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	
	public static JalContainer getFileStructure(File file) {
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
		}
		StringBuffer buf = new StringBuffer();
		String line="";
		try {
			while ((line=reader.readLine()) != null){
				buf.append(line+"\n");
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return getFileStructure(buf.toString());
	}
	
	
	public static String getMainFile(IProject prj) {
		ArrayList<IFile> result = new ArrayList<IFile>();
		String dir;

		dir = prj.getLocation().toOSString();
		File fdir = new File(dir);
		List<File> fileList = null;
		try {
			fileList = FileUtil.getFileListing(fdir);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		File[] files = (File[]) fileList.toArray(new File[fileList.size()]);
		
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().toLowerCase().endsWith(".jal")) {
					result.add(prj.getFile(files[i].getAbsolutePath().substring(prj.getLocation().toOSString().length())));
				}
			}
		}
		for (IFile file : result) {
			if (file != null && file.getLocation() != null)
				if (isMainFile(file.getLocation().toOSString()))
					return file.getLocation().toOSString();
		}
		JalConsole.write("Archivo principal no detectado.\nUsar comentario --;@main");			
		return null;
	}
	
	public static boolean isMainFile(String fileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line ="";
			while( (line = reader.readLine())!= null) {
				line = line.trim();
				if (line.equals("--;@main")) 
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null ) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;		
	}

	public static String getDeviceType(String fileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line ="";
			while( (line = reader.readLine())!= null) {
				line = line.trim();
				if (line.startsWith("const byte PICTYPE[]")) 
					return line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null ) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * Build a map wich contains availbale methods for a file,
	 * with method as key and method_file as value 
	 * @param file
	 * @includeMap all includes (including sub includes) for the file
	 * @param map the map that will be filled
	 */
	public static void buildTree(File file, Map<String, String> includesMap, Map<String, Object> map,
			boolean getTypedName, boolean strippseudomethod){
		
		JalContainer st = JalParser.getFileStructure(file);
		
		List<JalElement> elems = (List<JalElement>)st.getElements();
		for (Iterator<JalElement> it = elems.iterator();it.hasNext();){
			JalElement elem = (JalElement) it.next();
		
			if (elem instanceof JalContainer) {
				JalContainer container = (JalContainer) elem;
				if ("includes".equals(elem.getName())) {
					for (Iterator<JalElement> itCont = container.getElements().iterator();itCont.hasNext();){
						
						JalElement inc = (JalElement) itCont.next();	
						String incName = (String)includesMap.get(inc.getName().toLowerCase());
						
						if (incName == null) {
							System.out.println("unknown file : "+inc.getName());
						} else {
							//System.out.println(inc.getName()+" - "+incName );
							File f = new File(incName);
							buildTree(f, includesMap, map, getTypedName, strippseudomethod);
						}
					}
				} else if ("methods".equals(elem.getName())) {
					for (Iterator<JalElement> itCont = container.getElements().iterator();itCont.hasNext();){
						JalElement el = (JalElement) itCont.next();

						String name = getTypedName ? el.getTypedName() : el.getName();
						int pos = name.indexOf('\'');
						if (pos>=0 && strippseudomethod)
							name = name.substring(0, pos);

						el.setFilename(file.getAbsolutePath());							
						map.put(name, el);	
					}
				}
				else if ("vars".equals(elem.getName())||
						"consts".equals(elem.getName())||
						"aliases".equals(elem.getName())||
						"links".equals(elem.getName())) {
					for (Iterator<JalElement> itCont = container.getElements().iterator();itCont.hasNext();){
						JalElement var = (JalElement) itCont.next();

						String name = getTypedName ? var.getTypedName() : var.getName();
						int pos = name.indexOf('\'');
						if (pos>=0 && strippseudomethod)
							name = name.substring(0, pos);
						
						if (!map.containsKey(name))	{
							var.setFilename(file.getAbsolutePath());							
							map.put(name, var);
						}
					}
				}

			} else{
				map.put(getTypedName ? elem.getTypedName() : elem.getName(), file.getAbsolutePath());

			}

		}
	}
	
}
