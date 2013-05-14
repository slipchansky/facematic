package org.facematic.plugin.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class VelocityEngine {

	private Map binding = new HashMap();

	/**
	 * Put named value into context 
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public VelocityEngine put(String name, Object value) {
		binding.put(name, value);
		return this;
	}

	/**
	 * Put into context all named values from given map
	 * 
	 * @param m
	 * @return
	 */
	public VelocityEngine put(Map m) {
		for (Object k : m.keySet()) {
			put("" + k, m.get(k));
		}
		return this;
	}

	/**
	 * Translate string template
	 * @param body
	 * @return
	 */
	public String evaluateString(String body) {
		try {
			VelocityContext context = new VelocityContext(binding);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Writer w = new PrintWriter(out);
			Velocity.evaluate(context, w, "evaluateString", body);
			out.flush();
			w.close();
			String result = out.toString();
			return result;
		} catch (Exception e) {
			return body;
		}
	}

	/**
	 * Translate template from resource
	 * @param templateName
	 * @return
	 */
	public String evaluateTemplate(String templateName) {
		String result = evaluateString(StreamUtils
				.getResourceAsString("templates/" + templateName));
		if (result == null)
			result = "";
		return result;
	}
}
