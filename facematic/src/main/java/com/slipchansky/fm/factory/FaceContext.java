package com.slipchansky.fm.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.vaadin.ui.Component;

public class FaceContext implements Map {
	private Map context = new HashMap ();

	public int size() {
		return context.size();
	}

	public boolean isEmpty() {
		return context.isEmpty();
	}

	public boolean containsKey(Object key) {
		return context.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return context.containsValue(value);
	}

	public Object get(Object key) {
		return context.get(key);
	}

	public Object put(Object key, Object value) {
		return context.put(key, value);
	}

	public Object remove(Object key) {
		return context.remove(key);
	}

	public void putAll(Map m) {
		context.putAll(m);
	}

	public void clear() {
		context.clear();
	}

	public Set keySet() {
		return context.keySet();
	}

	public Collection values() {
		return context.values();
	}

	public Set entrySet() {
		return context.entrySet();
	}

	public boolean equals(Object o) {
		return context.equals(o);
	}

	public int hashCode() {
		return context.hashCode();
	}
	
	private String printRecursive (Map m, int offset) {
		if (offset > 10) return "\nerror}\n";
		
		String prefix = "";
		for (int i=0; i<offset; i++) {
		   prefix+="  ";	
		}
		String result = "{\n";
		int n = 0;
		for (Object key : context.keySet()) {
			if (n++ > 10) return "err1"; 
			result += prefix;
			if (key==FaceFactory.CONTEXT_PARENT_CONTEXT) {
				result+=key+"=parentContext=[...]";
			} else {
				if (context.get(key) instanceof Map) {
					HashMap mm = new HashMap (); mm.putAll((Map)context.get(key));
					mm.remove(FaceFactory.CONTEXT_CONTEXT);
					mm.remove(key);
					result+=key+"="+printRecursive (mm, offset+1);
				}
				else if ( context.get(key) instanceof Component ) {
					result+=key+"="+context.get(key).getClass().getSimpleName();
				}
				else
					result+=key+"="+context.get(key);
			}
			result+="\n";
		}
		result+=prefix+"}\n";
		return result;
		
	}

	@Override
	public String toString() {
		String result = "Map"+printRecursive(context, 0);
		return result;
	}

}
