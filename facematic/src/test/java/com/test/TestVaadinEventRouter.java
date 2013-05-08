package com.test;

import org.facematic.fmweb.plugin.FmAppUIforPluginPurposes;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class TestVaadinEventRouter {
	
	
	public static void main(String[] args) {
		
		VerticalLayout vl = new VerticalLayout();
		Button b = new Button ();
		
		b.addDetachListener(new ClientConnector.DetachListener() {
			
			@Override
			public void detach(DetachEvent event) {
				System.out.println("detached button"+event.getSource().getClass().getCanonicalName());
			}
		});
		
//		vl.addComponentDetachListener(new HasComponents.ComponentDetachListener() {
//			@Override
//			public void componentDetachedFromContainer(ComponentDetachEvent event) {
//				System.out.println("detached"+event.getDetachedComponent().getClass().getCanonicalName());
//			}
//		});
		
		vl.addComponent(b);
		
		
		
		UI ui = new FmAppUIforPluginPurposes ();
		
		ui.addComponentDetachListener(new HasComponents.ComponentDetachListener() {
			@Override
			public void componentDetachedFromContainer(ComponentDetachEvent event) {
				System.out.println("detached"+event.getDetachedComponent().getClass().getCanonicalName());
			}
		});
		ui.setContent(vl);
		Window w = new Window ();
		w.addCloseListener(new CloseListener () {

			@Override
			public void windowClose(CloseEvent e) {
				System.out.println ("window closed");
			}
			
		});
		
		
		
		
		ui.addWindow(w);
		ui.removeWindow(w);
		
		vl.removeComponent(b);
		
		vl.removeAllComponents();
		
		
	}

}
