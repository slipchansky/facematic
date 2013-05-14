package org.facematic.utils;

import java.io.IOException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.fmweb.plugin.FmAppUIforPluginPurposes;

import groovy.security.GroovyCodeSourcePermission;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GroovyEngine {
	private final static Logger logger = LoggerFactory.getLogger(GroovyEngine.class);
	
    private static final String GROOVY_DEFAUL_CODEBASE = "/groovy/shell";
	private Binding binding = new Binding ();
	private Script script;
	private Template template;
	
    
    private static class GroovyShellSingletonource {
    	final static GroovyShell GROOVY_SHELL = new GroovyShell();	
    }
	private static Map <String, Script>   preparedScripts = new ConcurrentHashMap<String, Script> ();
	private static Map <String, Template> preparedTemplates = new ConcurrentHashMap<String, Template> ();
	
    public GroovyEngine () {
        
    }
    
	private static String genMD5(String str) {
	    MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} 
		catch (NoSuchAlgorithmException e) {
		    e.printStackTrace();
		    return str;
		}   
		
	     md.update(str.getBytes());   
	     byte[] hash = md.digest();   
		 
	     StringBuffer hexString = new StringBuffer();
	     for (int i = 0; i < hash.length; i++) {   
	         if ((0xff & hash[i]) < 0x10) {   
	           hexString.append("0" + Integer.toHexString((0xFF & hash[i])));   
	         } else {   
	           hexString.append(Integer.toHexString(0xFF & hash[i]));   
	         }   
	       }      
	     
		 return hexString.toString();
	}
    
    
    /**
     * Поместить в контекст именованную переменную
     * @param name
     * @param value
     * @return
     */
    public GroovyEngine put (String name, Object value) {
        binding.setProperty(name, value);
        return this;
    }
    
    /**
     * Поместить в контекст именованные переменные из карты.
     * @param m
     * @return
     */
    public GroovyEngine put (Map m) {
        for (Object k : m.keySet()) {
            put (""+k, m.get(k));
        }
        return this;
    }
    
    private static Template getTemplate(String code) throws CompilationFailedException, ClassNotFoundException, IOException {
		String hash = genMD5(code);
		Template template = preparedTemplates.get (hash);
		if (template != null)
			return template;
		
	    return createTemplate(hash, code);
	}

	private static synchronized Template createTemplate(String hash, String code)
			throws ClassNotFoundException, IOException {
		Template template;
		template = new SimpleTemplateEngine().createTemplate(code);
		preparedTemplates.put(hash, template);
		return template;
	}
    


	/**
	 * Использование repareTemplate с последующим многократным использованием translate(-без-параметров-) или translate(Map variables)
	 * позволит сократить издержки на создании экземпляров template 
	 * 
	 * @param code
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void prepareTemplate(String code) throws ClassNotFoundException,
			IOException {
		template = getTemplate (code);
	}
	
	/**
	 * Использовать в связке с #prepareTemplate
	 * @param variables
	 * @return
	 */
	public String translate(Map variables) {
		if (template==null) return null;
		return template.make (variables).toString ();
	}
	
	/**
	 * Использовать в связке с #prepareTemplate
	 * @return
	 */
	public String translate() {
		if (template==null) return null;
		return template.make (binding.getVariables()).toString ();
	}
	

	/**
	 * Выполняет трансляцию специфицированного темплейта в контексте биндинга экземпляра GroovyEngine
	 * @param code
	 * @return
	 * @throws CompilationFailedException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public String translate (String code) throws CompilationFailedException, ClassNotFoundException, IOException {
		prepareTemplate (code);
        return translate ();
    }
	
	
    /**
     * Выполняет трансляцию специфицированного темплейта в контексте предоставленной карты переменных
     * @param code
     * @param variables
     * @return
     * @throws CompilationFailedException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public synchronized String translate (String code, Map variables) throws CompilationFailedException, ClassNotFoundException, IOException {
        if (code==null) return null;
        prepareTemplate(code);
        return translate(variables);
    }
	
    
    /**
     * Выполнение специфиированного скрипта
     * @param code
     * @return
     */
    public <T>T evaluate (String code) {
    	prepareScript(code);
    	return evaluate();
    }

	/**
	 * Использовамние prepareScript (String code) с последующим многократным выполнением evaluate (-без-параметров-) 
	 * позволит сэкономить на создании экземпляров ананимного класса выполняемого скрипта.  
	 * @param code
	 */
	public void prepareScript(String code) {
		script = getScript ( code );
	}

	/**
	 * Использовамние prepareScript (String code) с последующим многократным выполнением evaluate (-без-параметров-) 
	 * позволит сэкономить на создании экземпляров ананимного класса выполняемого скрипта.  
	 */
	public <T>T evaluate() {
		if (script==null) return null;
    	script.setBinding(binding);
    	return (T)script.run();
	}

    
    static int scriptNumber = 0;
    private static synchronized String generateScriptName() {
        return "Script" + (++scriptNumber) + ".groovy";
    }
    

    
    private static Script getScript(String code) {
		String hash = genMD5(code);
		Script script = preparedScripts.get (hash);
		
		if (script != null) {
			try {
				Object scriptObject = script.getClass ().newInstance();
				return (Script)scriptObject;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
        return createScript(hash, code);
	}

	private static synchronized Script createScript(String hash, String code) {
		Script script;
		
        final String scriptText = code;
		final String fileName = generateScriptName();
        SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
            sm.checkPermission(new GroovyCodeSourcePermission(GROOVY_DEFAUL_CODEBASE));
        }

        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>() {
            public GroovyCodeSource run() {
        		return new GroovyCodeSource(scriptText, fileName, GROOVY_DEFAUL_CODEBASE);
            }
        });
        
        script = GroovyShellSingletonource.GROOVY_SHELL.parse(gcs);
        preparedScripts.put (hash, script);
		return script;
	}

	/**
	 * Получить биндинг экземпляра GroovyEngine
	 * @return
	 */
	public Binding getBinding() {
        return binding;
    }

    /**
     * Получить шел экземпляра GroovyEngine
     * @return
     */
    public GroovyShell getShell() {
        return GroovyShellSingletonource.GROOVY_SHELL;
    }
}
