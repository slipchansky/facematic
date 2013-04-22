package com.slipchansky.fm.cdi;

import com.slipchansky.fm.ui.FacematicUI;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

public class FmUiProvider extends UIProvider {
	
	UIProvider original;
	UI ui;
	private FmServletService fmServletService;
	private FmCdiServlet fmServletInstance;
	private Class<? extends FacematicUI> uiClass;
	private String title;
	private String theme;
	private String widgetSet;
	
	FmUiProvider (UIProvider original) {
		this.original = original;
	}
	
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return original.getUIClass(event);
	}
	
	public UI createInstance(UICreateEvent event) {
		VaadinService service = event.getRequest().getService();
		
		if (! ( service instanceof FmServletService) ) {
			return ui = original.createInstance(event);
		}
		
		fmServletService = (FmServletService)service;
		fmServletInstance  = fmServletService.getServletInstance();
		
		Class <? extends UI> uiClass = fmServletInstance.getUiClass ();
		
		
		try {
			ui = uiClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return ui = original.createInstance(event);
		}
		
		if (! (ui instanceof FacematicUI) ) {
			return ui;
		}
		
		fmServletInstance.transferInjections((FacematicUI) ui);
		return ui;
	}
	
	public int hashCode() {
		return original.hashCode();
	}
	
	public String getTheme(UICreateEvent event) {
		if (theme != null)
			return theme;
			
		String theme = original.getTheme(event);
		if (theme==null) {
			theme = "reindeer";
		}
		return theme;
	}
	
	public String getWidgetset(UICreateEvent event) {
		if (this.widgetSet != null)
			return this.widgetSet;
		
		return original.getWidgetset(event);
	}
	
	public boolean equals(Object obj) {
		return original.equals(obj);
	}
	
	public boolean isPreservedOnRefresh(UICreateEvent event) {
		return original.isPreservedOnRefresh(event);
	}
	
	public String getPageTitle(UICreateEvent event) {
		if (title != null)
			return title;
		
		return original.getPageTitle(event); 
	}
	
	public String toString() {
		return original.toString();
	}
	
	

	public void init(FmSession fmSession) {
		VaadinService service = fmSession.getService();
		
		if (! ( service instanceof FmServletService) ) {
			return;
		}
		
		fmServletService   = (FmServletService)service;
		fmServletInstance  = fmServletService.getServletInstance();
		uiClass = fmServletInstance.getUiClass ();
		
        Title titleAnnotation = getAnnotationFor(uiClass, Title.class);
        if (titleAnnotation == null) {
            this.title = null;
        } else {
            this.title = titleAnnotation.value();
        }
        
        Theme uiTheme = getAnnotationFor(uiClass, Theme.class);
        if (uiTheme != null) {
            this.theme =  uiTheme.value();
        } else {
            this.theme = "reindeer";
        }
        
        Widgetset uiWidgetset = getAnnotationFor(uiClass, Widgetset.class);
        if (uiWidgetset != null) {
            this.widgetSet = uiWidgetset.value();
        } else {
    		String className = uiClass.getCanonicalName();
    		this.widgetSet = className.substring(0, className.length() - uiClass.getSimpleName ().length())+"AppWidgetSet";
        }
        
	}
	

}
