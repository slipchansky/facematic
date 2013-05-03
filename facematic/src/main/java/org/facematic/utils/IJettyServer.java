package org.facematic.utils;

public interface IJettyServer {
	int getPort ();
	void run(String home, int port, ClassLoader classLoader) throws Exception;
}
