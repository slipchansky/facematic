package org.facematic.cdi;

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

public class FmCdiServlet extends VaadinServlet {

	protected String widgetSetName;
	List<FmCdiEntryPoint> injections = new ArrayList<FmCdiEntryPoint> (); 
	
	List<FmCdiEntryPoint> getCdiInjections () {
		return injections;
	}
	
	public FmCdiServlet () {
	}
	
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) {
    	FmServletService servletService = new FmServletService(this, deploymentConfiguration);
        return servletService;
    }
    
    protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
        return new FmDeploymentConfiguration(this);
    }
	
	
	public void transferInjections (FacematicUI ui) {
		Field fields [] = getClass().getDeclaredFields();
		for (Field field : fields) {
			Inject injectAnnotation = field.getAnnotation(Inject.class);
			if (injectAnnotation != null) {
				injections.add (new FmCdiEntryPoint(field.getType(), FaceReflectionHelper.getFieldValue(this, field)));
			}
		}
		ui.setInjections (injections);
	}
	
	public Class<? extends FacematicUI> getUiClass () {
		return FacematicUI.class;
	}
	
	
}
