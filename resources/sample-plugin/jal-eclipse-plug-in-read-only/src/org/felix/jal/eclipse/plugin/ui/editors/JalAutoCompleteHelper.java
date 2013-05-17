package org.felix.jal.eclipse.plugin.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.ui.IFileEditorInput;
import org.felix.jal.lang.JalConstant;
import org.felix.jal.lang.JalMethod;
import org.felix.jal.lang.JalParser;


public class JalAutoCompleteHelper {
	static Map<String, String> includesMap = null;
	private JalAutoCompleteHelper() {}

	public static String[] getWords(String textToCurs, JalEditor editor) throws Exception{
		
		/*
		 * auto complete include
		 */
		textToCurs = textToCurs.trim();
		if (textToCurs.trim().toUpperCase().startsWith("INCLUDE")) {
				Set<String> keySet = includesMap.keySet();
				return keySet.toArray(new String[keySet.size()]);
		}		
		
		// find avaiable func / proc / vars
		Map<String, Object> all = new TreeMap<String, Object>();
		File file;
		
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
	
		String mainFile = JalParser.getMainFile(input.getFile().getProject());
		if (mainFile != null)
			file = new File(mainFile);
		else {
			mainFile = input.getFile().getLocation().toOSString();
			file = new File(mainFile);
		}

		includesMap = FileUtil.getIncludes(input.getFile().getProject(), mainFile);
		
		JalParser.buildTree(file, includesMap, all, true, true);
		
		/*
		 * Auto complete func and proc params 
		 */
		if (all.containsKey(textToCurs)) {
			
			if (all.get(textToCurs) instanceof JalMethod) {
				JalMethod elem= (JalMethod) all.get(textToCurs);
				return new String[]{"("+elem.getParams()+")"};
			}
		}
		
		List<String> allWords = new ArrayList<String>();
		
		for(Iterator<String> it = all.keySet().iterator();it.hasNext();){
			String name = it.next();
			allWords.add(name);
		}
		for (int i =0;i<JalConstant.KEYWORDS.length;i++){
			allWords.add(JalConstant.KEYWORDS[i]);
		}
		for (int i =0;i<JalConstant.KEYWORDS2.length;i++){
			allWords.add(JalConstant.KEYWORDS2[i]);
		}
		for (int i =0;i<JalConstant.KEYWORDS3.length;i++){
			allWords.add(JalConstant.KEYWORDS3[i]);
		}
		for (int i =0;i<JalConstant.TYPES.length;i++){
			allWords.add(JalConstant.TYPES[i]);
		}
		for (int i =0;i<JalConstant.BUILT_IN_FUNCTIONS.length;i++){
			allWords.add(JalConstant.BUILT_IN_FUNCTIONS[i]);
		}
		
		return (String[]) allWords.toArray(new String[allWords.size()]);
	}
}
