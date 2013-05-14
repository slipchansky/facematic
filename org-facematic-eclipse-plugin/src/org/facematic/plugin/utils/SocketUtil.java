package org.facematic.plugin.utils;

import java.io.IOException;
import java.net.Socket;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class SocketUtil {
	
	public static int getFreePort (int startPort) {
		for (int i = startPort, j=0; j<1000; i++, j++) {
            try {
				Socket echoSocket = new Socket("localhost", i);
				echoSocket.close();
			} catch (IOException e) {
				return i;
			}
		}
		return -1;
	}
	
	public static int getFreePort () {
		return getFreePort (8888);
	}
	
	
	public static void main(String[] args) {
		int port = getFreePort ();
		System.out.println (port);
	}

}
