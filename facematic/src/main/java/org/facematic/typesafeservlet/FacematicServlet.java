package org.facematic.typesafeservlet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;

import org.facematic.core.producer.FaceReflectionHelper;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class FacematicServlet extends VaadinServlet {

	protected String widgetSetName;
	
	public FacematicServlet () {
	}
	
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) {
    	FacematicServletService servletService = new FacematicServletService(this, deploymentConfiguration);
        return servletService;
    }
    
    protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
        return new FacematicDeploymentConfiguration(this);
    }
	
	
	public Class<? extends FacematicUI> getUiClass () {
		return FacematicUI.class;
	}
	
	
}
