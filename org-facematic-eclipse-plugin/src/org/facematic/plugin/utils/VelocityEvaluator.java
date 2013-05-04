package org.facematic.plugin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class VelocityEvaluator {

	private VelocityEngine engine;
	private VelocityContext context;

	public VelocityEvaluator() throws Exception {
		engine = new VelocityEngine();
		engine.init();
		context = new VelocityContext();
	}

	public VelocityEvaluator(Map m) throws Exception {
		engine = new VelocityEngine();
		engine.init();
		context = new VelocityContext(m);
	}

	public Object put(String key, Object value) {
		return context.put(key, value);
	}

	public String evaluateString(String toEval) {
		String result = toEval;
		try {
			OutputStream bos = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(bos);
			engine.evaluate(context, out, "velocity", toEval);
			out.close();
			bos.close();
			result = bos.toString();
			return result;
		} catch (Exception e) {
			return toEval;
		}

	}

	public InputStream evaluate(String templateName) throws Exception {

		String template = StreamUtils.getResourceAsString("templates/"
				+ templateName);
		OutputStream bos = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(bos);

		engine.evaluate(context, out, "velocity", template);

		out.close();
		bos.close();
		String result = bos.toString();
		return new ByteArrayInputStream(result.getBytes());
	}

	public void putAll(Map<String, String> substs) {
		for (String k : substs.keySet()) {
			put(k, substs.get(k));
		}

	}

}
