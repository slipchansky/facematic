package org.facematic.utils;

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
		byte [] bytes = outer.getBytes();
		int from = 1; 
		for (;from < bytes.length; from++) {
			if (bytes[from]=='>') {
				break;
			}
		}
		from++;
		int to = bytes.length-1;
		for (;to>from;to--) {
			if (bytes[to]=='<') {
				return outer.substring(from, to);
			}
		}
		return outer;
	}	
}
