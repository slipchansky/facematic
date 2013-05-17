package org.facematic.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

public class VelocityEngine implements ITemplateEngine {
	private Map<String, Object> binding = new HashMap<String, Object> ();
	
    public VelocityEngine () {
    }
    
    
    /* (non-Javadoc)
	 * @see org.facematic.utils.ITemplateEngine#put(java.lang.String, java.lang.Object)
	 */
    @Override
	public ITemplateEngine put (String name, Object value) {
        binding.put(name, value);
        return this;
    }
    
    /* (non-Javadoc)
	 * @see org.facematic.utils.ITemplateEngine#put(java.util.Map)
	 */
    @Override
	public ITemplateEngine put (Map m) {
        for (Object k : m.keySet()) {
            put (""+k, m.get(k));
        }
        return this;
    }
    


	/* (non-Javadoc)
	 * @see org.facematic.utils.ITemplateEngine#translate(java.lang.String)
	 */
	@Override
	public String translate (String code) {
		StringWriter w = new StringWriter  ();
		Context ctx = new VelocityContext(binding);
		Velocity.evaluate(ctx, w, "velocity", code);
		return w.toString();
    }
    
}
