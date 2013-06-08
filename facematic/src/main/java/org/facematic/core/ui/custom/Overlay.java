package org.facematic.core.ui.custom;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author Stanislav Lypchansky 
 *
 */
public class Overlay extends VerticalLayout {
	
    @Inject	
    @FmUI
    FacematicUI ui;
	
	private static final Logger logger = LoggerFactory.getLogger(Overlay.class);

	private Component currentComponentAfterWindowClosing;
	private List<Component>  elements = new ArrayList<Component> ();
	private List<String> captions = new ArrayList<String> ();
	protected Component currentComponent = null;
	TabSheet t;


	
	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractOrderedLayout#addComponent(com.vaadin.ui.Component)
	 */
	@Override
	public void addComponent(Component c) {
	    elements.add (c);
	    logger.trace ("elements.size = "+elements.size ());
	    logger.trace ("add: "+ c.getCaption());
	    captions.add(c.getCaption());
	    if (c instanceof Window) {
	    	logger.trace("Window added");
	    	Window w = (Window)c;
	    	w.addCloseListener(new Window.CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					updateCurrentAfterWindowClose ();
				}
			});
	    	return;
	    }
	    
	    c.setCaption (null);
	    
		super.addComponent(c);
		if (currentComponent==null) {
			currentComponent = c;
			fireSelectedElementChange();
		}
		else
			c.setVisible(false);
	}
	
	
	/**
	 * 
	 */
	protected void updateCurrentAfterWindowClose () {
		currentComponent = currentComponentAfterWindowClosing;
		
		if (currentComponent != null) {
		    currentComponent.setEnabled(true);
		}
		fireSelectedElementChange();
	}

	
	/**
	 * Adds element to overlay
	 * @param c
	 * @return
	 */
	public Component addElement (Component c) {
		addComponent (c);
		return c;
		
	}

	/**
	 * Returns element, placed at specified position
	 * @param position
	 * @return
	 */
	public Component getElement(int position) {
		return elements.get (position);
	}

	/**
	 * Makes specified element visible
	 * @param c
	 */
	public void showElement (Component c) {
		if (c == currentComponent)
			return;
		
		if (currentComponent instanceof Window ) {
			Window w = (Window)currentComponent;
			w.close();
		} 
			
		
		
		if (c instanceof Window) {
			if (ui != null) {
				Window w = (Window)c;
				if (currentComponent != null) {
					currentComponent.setEnabled(false);
				}
				currentComponentAfterWindowClosing = currentComponent; 
				currentComponent = c;
				ui.addWindow(w);
				fireSelectedElementChange();
			}
			return;
		}
		
		currentComponent.setVisible (false);
		c.setVisible(true);
		currentComponent = c;
		fireSelectedElementChange();
	}

	/**
	 * Makes position-specified element visible 
	 * @param position
	 */
	public void showElement (int position) {
		Component c = getElement(position);
		showElement (c);
	}

	/**
	 * Returns current visible element
	 * @return
	 */
	public Component getCurrent () {
		return currentComponent;
	}

	/**
	 * Returns caption of specified element
	 * @param component
	 * @return
	 */
	public String getElementCaption(Component component) {
		int index = elements.indexOf(component);
		return getElementCaption(index);
	}

	public String getElementCaption(int index) {
		if (index >=0 && index < elements.size())
			return captions.get(index);
		return null;
	}
	
    /**
     * Sends an event that the currently selected element has changed.
     */
    protected void fireSelectedElementChange() {
        fireEvent(new SelectedElementChangeEvent(this));
    }
	
	
    /**
     * Selected element change event. This event is sent when the selected (shown)
     * element in the overlay is changed.
     * 
     * @author Stanislav Lipchansky.
     */
	public static class SelectedElementChangeEvent extends Component.Event {
        private int elementPosition;

		/**
         * New instance of selected tab change event
         * @param source
         *            the Source of the event.
         */
        public SelectedElementChangeEvent(Overlay source) {
            super(source);
            this.elementPosition = source.elements.indexOf(source.getCurrent());
        }

        /**
         * Overlay where the event occurred.
         * @return the Source of the event.
         */
        public Overlay getOverlay() {
            return (Overlay) getSource();
        }

		public int getElementPosition() {
			return elementPosition;
		}
    }
	
    /**
     * Selected elemenyt change event listener. The listener is called whenever
     * another elemtny is selected, including when adding the first element to a
     * overlay.
     * 
     * @author Stanislav Lipchansky.
     */
    public interface SelectedElementChangeListener extends Serializable {

        /**
         * Selected (shown) Elemeny in overlay  has has been changed.
         * 
         * @param event
         *            the selected tab change event.
         */
        public void selectedElementChange(SelectedElementChangeEvent event);
    }
    
    private static final Method SELECTED_ELEMENT_CHANGE_METHOD;
    static {
        try {
            SELECTED_ELEMENT_CHANGE_METHOD = SelectedElementChangeListener.class.getDeclaredMethod("selectedElementChange", new Class[] { SelectedElementChangeEvent.class } );
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Overlay");
        }
    }    
	
	
    /**
     * Adds a overlay elemeny selection listener
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addSelectedElementChangeListener(SelectedElementChangeListener listener) {
        addListener(SelectedElementChangeEvent.class, listener, SELECTED_ELEMENT_CHANGE_METHOD);
    }


	public int getElementsCount() {
		return elements.size ();
	}


	public void setUi(FacematicUI ui) {
		this.ui = ui;
	}

}
