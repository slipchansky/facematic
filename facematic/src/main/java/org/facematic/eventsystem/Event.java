package org.facematic.eventsystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Event implements IEvent {
	private Object processedBy = null;
	Map<String, Object> bag = new ConcurrentHashMap<String, Object> (); 
	
	public void clear (Object by) {
		processedBy = by;
	}
	
	public boolean isCleared () {
		return processedBy != null;
	}

	public Object getProcessedBy() {
		return processedBy;
	}
	
	public void put (String key, Object value) {
		bag.put(key, value);
	}
	
	public <T> T get (String key) {
		return (T) bag.get(key);
	}
	
	public Map<String, Object> getBag () {
		return bag;
	}
	

}
