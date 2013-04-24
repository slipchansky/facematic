package org.facematic.cdi;

import java.util.Properties;

import org.facematic.core.ui.FacematicUI;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinServlet;

public class FmDeploymentConfiguration extends DefaultDeploymentConfiguration {
	
	public FmDeploymentConfiguration(Class<?> systemPropertyBaseClass, Properties initParameters) {
		super(systemPropertyBaseClass, initParameters);
	}

	public FmDeploymentConfiguration (FmCdiServlet servletInstance) {
		this (VaadinServlet.class, prepareConfiguration (servletInstance));
	}
	

	public static Properties prepareConfiguration(FmCdiServlet servletInstance) {
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
		properties.put ("productionMode", "false");
		return properties;
	}

	
};
