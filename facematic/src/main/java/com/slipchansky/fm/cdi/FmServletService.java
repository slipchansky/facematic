package com.slipchansky.fm.cdi;

import java.util.Properties;

import com.slipchansky.fm.ui.FacematicUI;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

import com.vaadin.server.VaadinSession;

class FmDeploymentConfigurationWrapper extends DefaultDeploymentConfiguration {
	
	public FmDeploymentConfigurationWrapper(Class<?> systemPropertyBaseClass, Properties initParameters) {
		super(systemPropertyBaseClass, initParameters);
	}

	public FmDeploymentConfigurationWrapper (FmCdiServlet servletInstance) {
		this (VaadinServlet.class, prepareConfiguration (servletInstance));
	}
	

	private static Properties prepareConfiguration(FmCdiServlet servletInstance) {
		Class<? extends FacematicUI> uiClass = servletInstance.getUiClass ();
        Title titleAnnotation = uiClass.getAnnotation(Title.class);
        String title = null;
        if (titleAnnotation != null) {
            title = titleAnnotation.value();
        }
        
        Theme uiTheme = uiClass.getAnnotation(Theme.class);
        String theme = null;
        if (uiTheme != null) {
            theme =  uiTheme.value();
        } else {
            theme = "reindeer";
        }
        
        Widgetset uiWidgetset = uiClass.getAnnotation(Widgetset.class);
        String widgetSet = null;
        if (uiWidgetset != null) {
            widgetSet = uiWidgetset.value();
        } else {
    		String className = uiClass.getCanonicalName();
    		widgetSet = className.substring(0, className.length() - uiClass.getSimpleName ().length())+"AppWidgetSet";
        }
	
        Properties properties;
        properties = new Properties ();
		properties.put ("UI",  servletInstance.getUiClass().getCanonicalName());
		properties.put ("widgetset", widgetSet);
		properties.put ("theme", theme);
		properties.put ("productionMode", "true");
		return properties;
	}

	
};



public class FmServletService extends VaadinServletService {

	FmCdiServlet servletInstance;
	
	public FmServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) {
		super(servlet, new FmDeploymentConfigurationWrapper((FmCdiServlet) servlet));
		servletInstance = (FmCdiServlet) servlet;
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) throws ServiceException {
		VaadinSession newSession = new FmSession (this);
		return newSession;
	}
	
	public FmCdiServlet getServletInstance () {
		return servletInstance;
	}
	

}
