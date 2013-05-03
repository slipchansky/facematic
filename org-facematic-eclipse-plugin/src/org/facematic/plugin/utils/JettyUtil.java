package org.facematic.plugin.utils;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;


public class JettyUtil implements IJettyServer {
	int    port;
	private Server server;
	private String webAppPath;
	
	public JettyUtil (String webAppPath) {
		this.webAppPath = webAppPath;
	}
	
	 /* (non-Javadoc)
	 * @see org.facematic.plugin.utils.IJettyServer#prepare(int, java.lang.ClassLoader)
	 */
	@Override
	public void prepare (int port, ClassLoader classLoader) throws Exception
	  {
		this.port = port;
	    server = new Server(port);
	    Class<? extends Servlet> fmInternalTestServletClass = (Class<? extends Servlet>)classLoader.loadClass("org.facematic.fmweb.servlet.FmInternalServletForPluginPurposes");
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(fmInternalTestServletClass, "/*"); 
        handler.setSessionHandler(new SessionHandler ());
        handler.setClassLoader(classLoader);
	    
	    server.setHandler(handler);
	  }
	 
	 

		/* (non-Javadoc)
		 * @see org.facematic.plugin.utils.IJettyServer#start()
		 */
		@Override
		public boolean start()  {
		    try {
				server.start();
			} catch (Exception e) {
			}
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.facematic.plugin.utils.IJettyServer#stop()
		 */
		@Override
		public void stop ()  {
			try {
			server.stop ();
			} catch (Exception e) {
			}
		}
		
		/* (non-Javadoc)
		 * @see org.facematic.plugin.utils.IJettyServer#restart()
		 */
		@Override
		public void restart (ClassLoader classLoader) {
			try {
				server.stop ();
			    server = new Server(port);
			    Class<? extends Servlet> fmInternalTestServletClass = (Class<? extends Servlet>)classLoader.loadClass("org.facematic.fmweb.servlet.FmInternalTestServlet");
		        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		        handler.addServlet(fmInternalTestServletClass, "/*"); 
		        handler.setSessionHandler(new SessionHandler ());
		        handler.setClassLoader(classLoader);
			    server.setHandler(handler);
			    server.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		/* (non-Javadoc)
		 * @see org.facematic.plugin.utils.IJettyServer#getPort()
		 */
		@Override
		public int getPort() {
			return port;
		}
}
