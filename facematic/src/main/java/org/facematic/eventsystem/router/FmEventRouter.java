package org.facematic.eventsystem.router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;

import org.facematic.eventsystem.IEvent;

/**
 * UI-wide Event Router
 * 
 * @author stas
 * 
 */
public class FmEventRouter {

	private Map<Class<? extends IEvent>, List<FmEventSubscriberEntry>> subscribersMap = new HashMap<Class<? extends IEvent>, List<FmEventSubscriberEntry>>();

	/**
	 * Adds subscriber to event router<br>
	 * using:
	 * 
	 * <pre>
	 * public class SomeController extends FmBaseController {
	 *    ...
	 *   &#64;FmViewComponent
	 *   VerticalLayout view
	 *   </pre><b>
	 *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#64;Inject<br>
	 *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FmEventRouter eventRouter;</b>
	 *   <pre>
	 *   
	 *   public void init () {
	 *   ...</pre>
	 *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>eventRouter.addSubscriber (this);</b><pre>   }
	 * 
	 *   public void handleEvent (SomeEvent ev) {<br>
	 *      if (!VaadinUtil.isViewAlive (view)) {
	 *          throw new InvalidSubscriberException ();
	 *      }
	 *      ...
	 *   }
	 *   ...
	 *   
	 *   public void doSomething () {
	 *      eventRouter.fire (new SomeAnotherEvent ());
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param potentialSubscriber
	 */
	public void addSubscriber(Object potentialSubscriber) {
		Method[] methods = potentialSubscriber.getClass().getDeclaredMethods();
		for (Method m : methods) {
			Class<?>[] types = m.getParameterTypes();

			if (types.length == 1) {
				if (IEvent.class.isAssignableFrom(types[0])) {
					addSubscriber((Class<? extends IEvent>) types[0],
							potentialSubscriber, m);
				}
			}
		}
	}

	public Object fire(IEvent event) throws IllegalAccessException {
		List<FmEventSubscriberEntry> subscribers = getSubscribers(event
				.getClass());
		if (subscribers == null) {
			return null;
		}

		List<FmEventSubscriberEntry> subscribersPull = new ArrayList(subscribers);

		for (FmEventSubscriberEntry entry : subscribersPull) {
			try {
				entry.handleEvent(event);
			} catch (InvocationTargetException e) {
				subscribers.remove(entry);
			}
			if (event.isCleared())
				return entry.getSubscriber();
		}
		return null;
	}

	private void removeSubscriber(Object potentialSubscriber) {
		Method[] methods = potentialSubscriber.getClass().getDeclaredMethods();
		for (Method m : methods) {
			Class<?>[] types = m.getParameterTypes();

			if (types.length == 1) {
				if (IEvent.class.isAssignableFrom(types[0])) {
					removeSubscriber((Class<? extends IEvent>) types[0],
							potentialSubscriber);
				}
			}
		}
	}

	private void removeSubscriber(Class<? extends IEvent> eventClass,
			Object subscriber) {
		List<FmEventSubscriberEntry> subscribers = subscribersMap.get(eventClass);
		if (subscribers == null) {
			return;
		}

		for (int i = subscribers.size() - 1; i >= 0; i--) {
			if (subscriber == subscribers.get(i).getSubscriber()) {
				subscribers.remove(i);
			}
		}
	}

	private List<FmEventSubscriberEntry> getSubscribers(
			Class<? extends IEvent> eventClass, boolean doAdd) {
		List<FmEventSubscriberEntry> subscribers = subscribersMap.get(eventClass);
		if (subscribers == null && doAdd) {
			subscribers = new ArrayList<FmEventSubscriberEntry>();
			subscribersMap.put(eventClass, subscribers);
		}
		return subscribers;
	}

	private List<FmEventSubscriberEntry> getSubscribers(
			Class<? extends IEvent> eventClass) {
		return getSubscribers(eventClass, false);
	}

	private void addSubscriber(Class<? extends IEvent> eventClass,
			Object subscriber, Method eventHandler) {
		getSubscribers(eventClass, true).add(
				new FmEventSubscriberEntry(subscriber, eventHandler));
	}
}
