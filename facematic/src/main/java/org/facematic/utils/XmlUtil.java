package org.facematic.utils;

import java.io.UnsupportedEncodingException;

import org.dom4j.Element;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class XmlUtil {

	/**
	 * Returns inner text/xml content of given xml node
	 * 
	 * @param element
	 * @return
	 */
	public static String getInnerContent (Element element) {
		String outer = element.asXML();
		return innerXmlTextContent (outer);
	}
	
	
	private static String innerXmlTextContent(String outer) {
		int openPos = outer.indexOf('>')+1;
		int closePos = outer.lastIndexOf('<');
		try {
		    return outer.substring(openPos, closePos);
		} catch (Exception e) {
			return null;
		}
	}

}
