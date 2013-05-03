package org.facematic.plugin.utils;

public interface IJettyServer {

	public abstract void prepare(int port, ClassLoader classLoader)
			throws Exception;

	public abstract boolean start();

	public abstract void stop();

	public abstract void restart(ClassLoader projectClassLoader);

	public abstract int getPort();

}