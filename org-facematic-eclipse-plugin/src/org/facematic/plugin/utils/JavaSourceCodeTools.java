package org.facematic.plugin.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author stas
 * 
 */
public class JavaSourceCodeTools {
	final String INDENT = "    "; 
	private Map<String, String> existingMethods = new WeakHashMap<String, String>();
	private List<String> methods;
	private int codeEndPosition;
	private JavaCodeAnalizer analizer;
    
	private JavaSourceCodeTools(JavaCodeAnalizer analizer) {
		this.analizer = analizer;
		methods = analizer.getMethodsNames();
		for (String m : methods) {
			existingMethods.put(m, "");
		}
		codeEndPosition = analizer.finish;
	}

	public boolean methodExists(String name) {
		return existingMethods.get(name) != null;
	}

	public List<String> getMethods() {
		return methods;
	}
	
	public void addMethod (String methodName, Class parameterType, String parameterName) {
		if (methodExists (methodName)) {
			return;
		}
		String method = "\n"+INDENT+"@FmReaction\n"+INDENT +"public void "+methodName+" (";
		if (parameterType != null) {
			method += parameterType.getCanonicalName()+" "+parameterName;
		}
		method += ") {\n";
		method+= INDENT+" // TODO add your code here \n";
		method+=INDENT +"}\n";
		analizer.parent.sourceCodeBuilder.insert(codeEndPosition-1, method);
		codeEndPosition += method.length();
		existingMethods.put(methodName, "");
		methods.add(methodName);
	}
	
	public String toString () {
		return analizer.parent.sourceCodeBuilder.toString();
	}
	
	public static JavaSourceCodeTools getHandler (String code) {
		JavaCodeAnalizer util = new JavaCodeAnalizer(code);
		util.scan(0);
		return util.getClassCode();
	}
	
	
	////////////////////////////////////////////////
	/**
	 * Java Code Analizer
	 * @author stas
	 *
	 */
	private static class JavaCodeAnalizer {
		private List<JavaCodeAnalizer> nesteds = new ArrayList();
		private byte[] sourceBytes;
		private int scanPostition = 0;

		private int start;
		private int finish;
		private String declaration = "";
		private StringBuilder sourceCodeBuilder;
		private JavaCodeAnalizer parent; 

		private JavaCodeAnalizer(String source) {
			sourceCodeBuilder = new StringBuilder (source);
			sourceBytes = source.getBytes();
			this.start = 0;
			this.finish = sourceBytes.length;
		}

		private JavaCodeAnalizer(JavaCodeAnalizer parent, String declaration) {
			this.parent = parent;
			this.declaration = declaration;
			sourceBytes = parent.sourceBytes;
			scanPostition = parent.scanPostition;
			this.start = 0;
			this.finish = sourceBytes.length;
		}

		private List<JavaCodeAnalizer> getNesteds() {
			return nesteds;
		}

		private int scan(int level) {
			StringBuilder tokenBuilder = new StringBuilder();

			for (; scanPostition < sourceBytes.length;) {
				byte b = sourceBytes[scanPostition++];
				if (b == '{') {
					JavaCodeAnalizer nested = new JavaCodeAnalizer(this,
							tokenBuilder.toString().trim());
					tokenBuilder = new StringBuilder();
					scanPostition = nested.scan(level + 1);
					nesteds.add(nested);
					continue;
				}

				if (b == '}') {
					finish = scanPostition;
					return scanPostition;
				}

				tokenBuilder.append((char) b);

				if (b == ';') {
					String declarration = tokenBuilder.toString().trim();
					declarration = declarration.substring(0,
							declarration.length() - 1);
					tokenBuilder = new StringBuilder();
				}
			}
			return scanPostition;
		}

		private boolean isClass() {
			String classToken = "class";
			String publicToken = "public";

			if (declaration.indexOf(classToken) >= 0) 
				if (declaration.indexOf (publicToken) >= 0)
					if (declaration.indexOf (publicToken) < declaration.indexOf(classToken) ) {
						return true;
			}
			return false;
		}

		private List<String> getMethodsNames() {
			List<String> methods = new ArrayList();
			for (JavaCodeAnalizer jc : nesteds) {
				String m = jc.declaration;
				String a[] = m.split("\\(");
				if (a.length < 2)
					continue;
				m = a[a.length - 2];
				byte b[] = m.trim().getBytes();
				StringBuilder methodNameBuilder = new StringBuilder();
				for (int i = b.length - 1; i >= 0; i--) {
					char c = (char) b[i];
					if (c == ' ' || c == '\t')
						break;
					else
						methodNameBuilder.insert(0, c);

				}
				methods.add(methodNameBuilder.toString());
			}
			return methods;
		}

		private JavaSourceCodeTools getClassCode() {
			for (JavaCodeAnalizer a : nesteds) {
				if (a.isClass()) {
					return new JavaSourceCodeTools(a);
				}
			}
			return null;
		}
	}
	
	

	public static void main(String[] args) {
		//String code = "public class Clazz extends X {public void amethod(){...} public void bmethod(){...}} ";
		String code = StreamUtils.getResourceAsString("org/test/FaceProducer.txt");

		JavaSourceCodeTools tools = JavaSourceCodeTools.getHandler(code);
		tools.addMethod("test", null, null);
		tools.addMethod("onemore", String.class, "name");
		
        System.out.println (""+tools);

	}

}
