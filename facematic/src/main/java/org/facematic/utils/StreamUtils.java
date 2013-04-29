package org.facematic.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarInputStream;

/**
 * @author S.Lipchansky
 * The set of iostream utilite functions
 */
public class StreamUtils {
	
	private static ResourceAccessor resourceAccessor = null;


	/**
	 * Gets cachable/noncachable input stream for given URL
	 * @param surl - String for url
	 * @param enableCache - Caching ebabled or disabled
	 * @return URL.openStream ()
	 * @throws IOException
	 */
	public static InputStream getUrlStream (String surl, Boolean enableCache) throws IOException {
		URL url = new URL (surl);
		url.openConnection().setDefaultUseCaches(false);
		return url.openStream();
	}

	/**
	 * Copies content of InputStream to OutputStream
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static int copy (InputStream in, OutputStream out) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in, 4096);
		BufferedOutputStream bos = new BufferedOutputStream(out, 4096);
		int length = 0;
		int result = bis.read();
	    while(result != -1) {
	      bos.write(result);
	      result = bis.read();
	      length++;
	    }
	    bos.flush();
//	    bos.close ();
	    bis.close ();

	    return length;
	}

	/**
	 * Returns bytes [] content of InputStream
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte [] getBytes (InputStream in) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in, 4096);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
	    int result = bis.read();
	    while(result != -1) {
	      byte b = (byte)result;
	      buf.write(b);
	      result = bis.read();
	    }
	    buf.flush();
	    byte [] bytes = buf.toByteArray();
	    buf.close ();
	    return bytes;
	}

	/**
	 * Returns String content of InputStream
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String getString (InputStream in) throws IOException {
		byte [] bytes = getBytes (in);
		return new String (bytes, "UTF-8");
	}

	/**
	 * Gets InputStream using currentThread class loader
	 * @param path - resource path
	 * @return InputStream
	 */
	public static InputStream classLoaderInputStram (String path) {
		if (resourceAccessor != null) {
			return resourceAccessor.getResourceStream(path);
		}
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}

	/**
	 * Gets InputStream using given class classLoader
	 * @param path - resource path
	 * @return InputStream
	 */
	public static InputStream classLoaderInputStram (Class host, String path) {
		return host.getClassLoader().getResourceAsStream(path);
	}

	public static String getResourceAsString (String path) {
		InputStream in = classLoaderInputStram (path);
		if (in==null) return null;
		String resource;
		try {
			resource = getString (in);
			in.close ();
		} catch (IOException e) {
			return null;
		}

		return resource;
	}

	public static void setResourceAccessor(ResourceAccessor resourceAccessor) {
		StreamUtils.resourceAccessor = resourceAccessor;
	}
}
