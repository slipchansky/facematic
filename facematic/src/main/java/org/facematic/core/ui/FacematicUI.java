package org.facematic.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.facematic.cdi.FmCdiEntryPoint;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.producer.FaceReflectionHelper;
import org.facematic.eventsystem.IEvent;
import org.facematic.eventsystem.router.FmEventRouter;

import com.vaadin.ui.UI;

public abstract class FacematicUI extends UI {
	private List<FmCdiEntryPoint> injections;
	protected FaceProducer producer;
	private FmEventRouter eventRouter = new FmEventRouter ();
	
	
	public FacematicUI () {
		producer =new FaceProducer (this, this);
	}
	
	public List<FmCdiEntryPoint>  getInjections () {
		return injections;
	}
	
	public void setInjections (List<FmCdiEntryPoint> injections) {
		this.injections = injections;
		List<FmCdiEntryPoint> injectionsWithEventRouter = new ArrayList<FmCdiEntryPoint> (injections);
		injectionsWithEventRouter.add(new FmCdiEntryPoint(FmEventRouter.class, eventRouter));
		applyInjections  (injectionsWithEventRouter);
	}

	private void applyInjections(List<FmCdiEntryPoint> injections) {
		FaceReflectionHelper reflectionHelpler = new FaceReflectionHelper (this);
		reflectionHelpler.addUiInjections (this);
	}

	/**
	 * Adds subscriber to event router<br>
	 * using:
	 * 
	 * <pre>
	 * public class SomeController extends FmBaseController {
	 *    ...
	 *   &#64;FmViewComponent
	 *   VerticalLayout view
	 *   
	 *   @FmUi
	 *   FacematicUI ui;
	 *   
	 *   public void init () {
	 *   ...</pre>
	 *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>ui.addSubscriber (this);</b><pre>   }
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
	public void addSubscriber (Object potentialSubscriber) {
		eventRouter.addSubscriber(potentialSubscriber);
	}

	public Object fireEvent (IEvent event) throws IllegalAccessException {
		return eventRouter.fire(event);
	}
	
	
	

}
