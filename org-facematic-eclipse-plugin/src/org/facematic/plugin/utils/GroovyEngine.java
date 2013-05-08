package org.facematic.plugin.utils;

import java.io.IOException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.control.CompilationFailedException;

import groovy.security.GroovyCodeSourcePermission;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GroovyEngine {
	
	class NoErrorMap extends HashMap {
		@Override
		public Object get(Object arg0) {
			Object result = super.get(arg0);
			if (result == null) { 
				result = "${"+arg0+"}";
				put (arg0, result);
			}
			
			return result;
		}
	}
		
	
    private static final String GROOVY_DEFAUL_CODEBASE = "/groovy/shell";
	private Script script;
	private Template template;
	NoErrorMap binding = new NoErrorMap ();
	
    
    private static class GroovyShellSingletonource {
    	final static GroovyShell GROOVY_SHELL = new GroovyShell();	
    }
    
	private static Map <String, Template> preparedTemplates = new ConcurrentHashMap<String, Template> ();
	
    
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
        binding.put(name, value);
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
		return template.make (binding).toString ();
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
     * Получить шел экземпляра GroovyEngine
     * @return
     */
    public GroovyShell getShell() {
        return GroovyShellSingletonource.GROOVY_SHELL;
    }
    
    public static void main(String[] args) throws CompilationFailedException, ClassNotFoundException, IOException {
		GroovyEngine e = new GroovyEngine();
		e.put("mama", "rama");
		String result = e.translate("test ${mama} $hui rama");
		System.out.println (result);
	}



	public String evaluateString(String body) {
		try {
			return translate (body);
		} catch (Exception e) {
			System.out.println (body);
			return body;
		}
	}
	
	public String evaluateTemplate(String templateName) {
		String result = evaluateString (StreamUtils.getResourceAsString("templates/"+templateName));
		if (result==null) result = "";
		return result;
	}
}
