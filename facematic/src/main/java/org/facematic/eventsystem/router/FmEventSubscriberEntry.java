package org.facematic.eventsystem.router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.facematic.eventsystem.IEvent;

public class FmEventSubscriberEntry {
	private Object subscriber;
	private Method eventHandler;
	
	public FmEventSubscriberEntry(Object subscriber, Method eventHandler) {
		super();
		this.subscriber = subscriber;
		this.eventHandler = eventHandler;
	}
	
	public void handleEvent (IEvent event) throws IllegalAccessException, InvocationTargetException {
		try {
			eventHandler.invoke(subscriber, event);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public Object getSubscriber() {
		return subscriber;
	}

}
