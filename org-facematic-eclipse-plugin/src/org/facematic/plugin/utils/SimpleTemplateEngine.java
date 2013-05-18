package org.facematic.plugin.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * @author "Stanislav Lipchansky"
 *
 */
public class SimpleTemplateEngine {
	
	private Map binding = new HashMap();
	
	public SimpleTemplateEngine () {
		binding.put("HASH", "#");
	}

	/**
	 * Put named value into context 
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public SimpleTemplateEngine put(String name, Object value) {
		binding.put(name, value);
		return this;
	}

	/**
	 * Put into context all named values from given map
	 * 
	 * @param m
	 * @return
	 */
	public SimpleTemplateEngine put(Map m) {
		for (Object k : m.keySet()) {
			put(k.toString(), m.get(k));
		}
		return this;
	}

	/**
	 * Translate string template
	 * @param body
	 * @return
	 */
	public String evaluateString(String body) {
		if (body.indexOf('$')<0) return body;
		StringBuilder result = new StringBuilder ();
		StringBuilder nameBuilder = new StringBuilder ();
		byte b [] = body.getBytes ();
		for (int i=0; i<b.length; i++ ) {
			char c = (char)b[i];
			if (c=='$' &&i<b.length-2 && b[i+1]=='{' ) {
				nameBuilder.setLength(0);
				for (i+=2;i<b.length;i++) {
					if (b[i]=='}') break;
					else
						nameBuilder.append((char)b[i]);
				}
				String name = nameBuilder.toString();
				if (!"".equals(name)) {
					Object value = binding.get (name);
					if (value==null) {
						value = "${"+name+'}';
					}
					result.append(value.toString());
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	/**
	 * Translate template from resource
	 * @param templateName
	 * @return
	 */
	public String evaluateTemplate(String templateName) {
		return evaluateString(StreamUtils.getResourceAsString("templates/" + templateName));
	}
	
}
