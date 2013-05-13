package org.facematic.typesafeservlet;

import java.util.Properties;

import org.facematic.core.ui.FacematicUI;
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





public class FacematicServletService extends VaadinServletService {

	FacematicServlet servletInstance;
	
	public FacematicServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) {
		super(servlet, new FacematicDeploymentConfiguration((FacematicServlet) servlet));
		servletInstance = (FacematicServlet) servlet;
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) throws ServiceException {
		VaadinSession newSession = new FacematicSession (this);
		return newSession;
	}
	
	public FacematicServlet getServletInstance () {
		return servletInstance;
	}
	

}
