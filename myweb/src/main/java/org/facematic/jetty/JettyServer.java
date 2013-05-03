package org.facematic.jetty;
/***
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.TagLibConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.facematic.fmweb.servlet.FmInternalTestServlet;
import org.facematic.utils.IJettyServer;
import org.facematic.utils.StreamUtils;



public class JettyServer {
	private int port;
	Server    server;

	public JettyServer () {
	}
	 
	 
	 public void run (int port) throws Exception
	  {
		this.port = port;
	    server = new Server(port);
	    WebAppContext context = new WebAppContext();
	    
	    context.setParentLoaderPriority(true);
	    context.setContextPath("/");
	    context.setResourceBase("src/main/webapp/"); //webApp
	    //root.setExtraClasspath("target/classes/");
	    context.setConfigurations(new Configuration[] {
               new AnnotationConfiguration(), new WebXmlConfiguration(),
               new WebInfConfiguration(), new TagLibConfiguration(),
               new PlusConfiguration(), new MetaInfConfiguration(),
               new FragmentConfiguration(), new EnvConfiguration() });
	    
	    Servlet servlet = new FmInternalTestServlet ();
	    context.addServlet(new ServletHolder(servlet), "/*");
	    server.setHandler(context);
	    server.start();
	 
//	    new Thread (
//	    	new Runnable () {
//				@Override
//				public void run() {
//					try {
//						server.start();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//	    		
//	    	}).start();
	  }
	 
	 
	 public void stop () {
		 if (server != null)
			 if (server.isStarted()) {
				try {
					server.stop ();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
	 }
	 
	 public void restart () throws Exception {
		 stop ();
		 run (port);
	 }


	 
	 

}
**/