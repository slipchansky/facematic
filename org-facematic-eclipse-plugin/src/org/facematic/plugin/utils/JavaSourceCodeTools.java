package org.facematic.plugin.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.facematic.utils.StreamUtils;

/**
 * @author stas
 * 
 */
public class JavaSourceCodeTools {
	final String INDENT = "    ";
	private Map<String, String> existingMethods = new WeakHashMap<String, String>();
	private Map<String, String> existingFields = new WeakHashMap<String, String>();
	private Map<String, String> existingImports = new WeakHashMap<String, String>();

	private List<String> methods;

	private int codeEndPosition;
	private int importsPosition;
	private int fieldsPosition;

	private JavaCodeAnalizer analizer;

	private JavaSourceCodeTools(JavaCodeAnalizer analizer) {
		this.analizer = analizer;
		methods = analizer.getMethodsNames();
		for (String m : methods) {
			existingMethods.put(m, "");
		}

		for (String f : analizer.fields) {
			existingFields.put(f, "");
		}

		for (String i : analizer.parent.imports) {
			existingImports.put(i, "");
		}

		codeEndPosition = analizer.finish;
		importsPosition = analizer.parent.importsStart;
		fieldsPosition = analizer.fieldsStart;

	}

	public boolean methodExists(String name) {
		return existingMethods.get(name) != null;
	}

	public List<String> getMethods() {
		return methods;
	}

	
	public void setListener(String methodName, Class parameterType, String parameterName, Class producerClass, String producerName, String producerCaption, String eventName) {
		if (methodExists(methodName)) {
			return;
		}
		String method = "\n" + INDENT + "@FmReaction(\""+producerClass.getSimpleName();
		
		
		if (producerName == null || "".equals(producerName)) {
			if (producerCaption==null || "".equals(producerCaption)) {
				producerName = "#noname";
			} else
				producerName = "[caption='"+producerCaption+"']";				
		} else 
			producerName = '#'+producerName;
			
		method+=producerName+'.'+eventName;
				method +="\")\n"+ INDENT + "public void " + methodName + " (";
		if (parameterType != null) {
			method += parameterType.getSimpleName() + " " + parameterName;
		}
		method += ") {\n";
		method += INDENT + " // TODO add your code here \n";
		method += INDENT + "}\n";
		analizer.parent.sourceCodeBuilder.insert(codeEndPosition - 1, method);
		codeEndPosition += method.length();
		existingMethods.put(methodName, "");
		methods.add(methodName);
	}

	public void addImport(String pkg) {
		if (existingImports.get(pkg) != null)
			return;
		String importText = "\nimport " + pkg + ";";
		analizer.parent.sourceCodeBuilder.insert(importsPosition, importText);
		importsPosition += importText.length();
		fieldsPosition += importText.length();
		codeEndPosition += importText.length();
	}

	public void addField(String field, String type, String annotation) {
		if (existingFields.get(field) != null)
			return;
		String fielText = "\n";
		if (annotation != null) {
			fielText += "\n"+INDENT + annotation + "\n";
			fielText += INDENT + type + " " + field + ";";
		}

		analizer.parent.sourceCodeBuilder.insert(fieldsPosition, fielText);
		if (codeEndPosition > fieldsPosition) {
			codeEndPosition += fielText.length();
		}
		fieldsPosition += fielText.length();
	}

	public String toString() {
		return analizer.parent.sourceCodeBuilder.toString();
	}

	public static JavaSourceCodeTools getHandler(String code) {
		JavaCodeAnalizer analizer = new JavaCodeAnalizer(code);
		analizer.scan(0);

		return analizer.getClassCode();
	}

	// //////////////////////////////////////////////
	/**
	 * Java Code Analizer
	 * 
	 * @author stas
	 * 
	 */
	private static class JavaCodeAnalizer {
		private List<JavaCodeAnalizer> nesteds = new ArrayList();
		private byte[] sourceBytes;
		private int scanPostition = 0;

		List<String> fields = new ArrayList<String>();
		List<String> methods = new ArrayList<String>();
		List<String> imports = new ArrayList<String>();

		private int start;
		private int finish;
		private String declaration = "";
		private StringBuilder sourceCodeBuilder;
		private JavaCodeAnalizer parent;

		int fieldsStart = 0;
		int methodsStart = 0;
		int importsStart = 0;
		boolean methodsFound = false;

		private JavaCodeAnalizer(String source) {
			sourceCodeBuilder = new StringBuilder(source);
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

				if (b == '*')
					if (scanPostition > 1)
						if (sourceBytes[scanPostition - 2] == '/') {
							findEoBlockComment();
							continue;
						}

				if (b == '/')
					if (sourceBytes[scanPostition] == '/') {
						findEoInlineComment();
						continue;
					}

				if (b == '{') {
					String decl = tokenBuilder.toString();
					JavaCodeAnalizer nested = new JavaCodeAnalizer(this,
							tokenBuilder.toString().trim());
					tokenBuilder = new StringBuilder();
					scanPostition = nested.scan(level + 1);
					nesteds.add(nested);
					if (isField(decl)) {
						String fieldName = getFieldName(decl);
						if (fieldName != null)
							fields.add(fieldName);
					} else if (isMethod(decl)) {
						methodsFound = true;
						String methodName = getMethodname(decl);
						if (methodName != null)
							methods.add(methodName);
					}
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
					if (isImport(declarration)) {
						importsStart = scanPostition;
						String importName = getImortName(declarration);
						if (importName != null)
							imports.add(importName);
					} else if (isField(declarration)) {
						if (!methodsFound)
							fieldsStart = scanPostition;

						String fieldName = getFieldName(declarration);
						if (fieldName != null)
							fields.add(fieldName);

					}
					tokenBuilder = new StringBuilder();
				}
			}
			return scanPostition;
		}

		private int findEoInlineComment() {
			for (scanPostition++; scanPostition < sourceBytes.length;) {
				byte b = sourceBytes[scanPostition++];
				if (b == '\n')
					return scanPostition;
			}
			return 0;
		}

		private int findEoBlockComment() {
			for (; scanPostition < sourceBytes.length;) {
				byte b = sourceBytes[scanPostition++];
				if (b == '/' && sourceBytes[scanPostition - 2] == '*')
					return scanPostition;
			}

			return sourceBytes.length;
		}

		private String getImortName(String declarration) {
			String a[] = declarration.split(" ");
			if (a.length < 2)
				return null;
			return a[a.length - 1].trim();
		}

		private boolean isField(String declarration) {
			byte[] b = declarration.getBytes();
			int i;
			for (i = b.length - 1; i >= 0; i--) {
				if (b[i] == '(' || b[i] == ')')
					return false;
				else if (b[i] == ' ' || b[i] == '\t')
					break;
			}
			int classPos = declarration.indexOf("class");
			if (classPos == 0)
				return false;
			if (classPos > 0) {
				if (b[classPos - 1] == '.')
					if (!(declarration.indexOf("=") < classPos && declarration
							.indexOf("=") > 0))
						return false;

			}

			return declarration.indexOf("package") < 0
					&& declarration.indexOf("interface") < 0;
		}

		private boolean isMethod(String declarration) {
			String a[] = declarration.split("\\(");
			if (a.length < 2)
				return false;
			return declarration.indexOf("=") < 0;
		}

		private boolean isImport(String declaration) {
			return (declaration.indexOf("import") >= 0);
		}

		String getMethodname(String declaration) {
			String a[] = declaration.split("\\(");
			if (a.length < 2)
				return null;
			declaration = a[a.length - 2];
			byte b[] = declaration.trim().getBytes();
			StringBuilder methodNameBuilder = new StringBuilder();
			for (int i = b.length - 1; i >= 0; i--) {
				char c = (char) b[i];
				if (c == ' ' || c == '\t')
					break;
				else
					methodNameBuilder.insert(0, c);
			}
			return methodNameBuilder.toString();
		}

		String getFieldName(String declaration) {
			int eqPos = declaration.indexOf('=');
			int bracePos = declaration.indexOf(')');

			if (bracePos < eqPos) {
				String a[] = declaration.split("=");
				if (a.length > 1) {
					declaration = a[0].trim();
				}
			}

			byte b[] = declaration.trim().getBytes();
			StringBuilder fieldNameBuilder = new StringBuilder();
			for (int i = b.length - 1; i >= 0; i--) {
				char c = (char) b[i];
				if (c == ' ' || c == '\t')
					break;
				else
					fieldNameBuilder.insert(0, c);
			}
			return fieldNameBuilder.toString();
		}

		private boolean isClass() {
			String classToken = "class";
			String publicToken = "public";

			if (declaration.indexOf(classToken) >= 0)
				if (declaration.indexOf(publicToken) >= 0)
					if (declaration.indexOf(publicToken) < declaration
							.indexOf(classToken)) {
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
	

//	public static void main(String[] args) {
//		//String code = "public class Clazz extends X {public void amethod(){...} public void bmethod(){...}} ";
//		String code = StreamUtils.getResourceAsString("org/test/FaceProducer.txt");
//		
//		String str = code.substring(4794);
//
//		JavaSourceCodeTools tools = JavaSourceCodeTools.getHandler(code);
//		tools.addMethod("test", null, null);
//		tools.addMethod("onemore", String.class, "name");
//		tools.addField ("huj", "String", "@Inject");
//		tools.addField ("huj", "String", null);
//		tools.addImport ("com.custvox.view");
//		tools.addImport ("com.custvox.huj");
//		
//        System.out.println (""+tools);
//
//	}

}
