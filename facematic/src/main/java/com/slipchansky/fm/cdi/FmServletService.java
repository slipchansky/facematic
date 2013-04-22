package com.slipchansky.fm.cdi;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

public class FmServletService extends VaadinServletService {

	FmCdiServlet servletInstance;
	
	public FmServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) {
		super(servlet, deploymentConfiguration);
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
