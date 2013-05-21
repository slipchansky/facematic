package org.facematic.core.producer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultDocument;
import org.facematic.core.logging.LoggerFactory;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class ExtensionProcessor {

	private static Logger logger = LoggerFactory.getLogger(ExtensionProcessor.class);

	private Map<String, Element> placeholders = new HashMap<String, Element> (); 
	
	/**
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public Document processXml (String xml) throws Exception {
		Element result = process (xml);
		for (String key : placeholders.keySet()) {
			Element e = placeholders.get(key);
			e.getParent().remove(e);
		}
		return new DefaultDocument(result);
	}
	
	public Element processResource (String resourceName) throws Exception {
		String xml = getXmlResource(resourceName);
		Document doc = processXml (xml);
		return doc.getRootElement();
	}
	
	/**
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Element processFromResource (String resourceName) throws Exception {
		String xml = getXmlResource(resourceName);
		return  process (xml);
	}

	public String getXmlResource(String resourceName) {
		String xml = org.facematic.utils.StreamUtils.getResourceAsString(resourceName);
		if (xml == null) {
			resourceName = resourceName.replaceAll("\\.", "/") + ".xml";
			logger.info("Look for resource " + resourceName);
			xml = org.facematic.utils.StreamUtils
					.getResourceAsString(resourceName);

		}

		if (xml == null) {
			logger.error("Cannot find resource " + resourceName);
			return null;
		}
		return xml;
	}
	

	private Element process(String xml) throws Exception {

		Element result = processString(xml);
		return result;
	}

	private Element processString(String xml) throws Exception {
		if (xml == null) {
			return null;
		}
		Document document = DocumentHelper.parseText(xml);
		Element root = processRoot(document.getRootElement());
		return root;
	}

	private Element processRoot(Element root) throws Exception {
		if (!"extension".equals(root.getName())) {
			return root;
		}

		Element zuper = processFromResource(root.attributeValue("base"));

		Element extension = root;
		root = zuper;

		
		collectPlaceHolders(root);

		for (Element implement : (List<Element>) extension.elements("implement")) {
			String phname = implement.attributeValue("placeholder");
			Element placeholder = placeholders.get(phname);
			if (placeholder == null) {logger.warn("Couldn't find placeholder for implementation: "	+ phname);
				continue;
			}

			Element inner = processString(implement.asXML());
			for (Element e : (List<Element>) inner.elements()) {
				e.getParent().remove(e);
				List elements = placeholder.getParent().elements();
				elements.add(elements.indexOf(placeholder), e);
			}
		}
		
		for (Attribute a : (List<Attribute>)extension.attributes ()) {
			if (root.attribute(a.getName()) != null) {
				root.attribute(a.getName()).setValue(a.getValue());
			} else {
				root.addAttribute(a.getName(), a.getValue());
			}
			
		}

		return root;
	}

	private void collectPlaceHolders(Element node) {
		for (Element e : (List<Element>) node.elements()) {
			if ("placeholder".equals(e.getName())) {
				String placeholderName = e.attributeValue("name");
				if (placeholders.get(placeholderName) != null) {
					logger.error("Extension hierarchy contains not-unique placeholder name:"
							+ placeholderName);
					continue;
				}
				placeholders.put(placeholderName, e);
			}
			collectPlaceHolders(e);
		}
	}

	
	static String format(Element root) throws IOException {
		Document document = new DefaultDocument(root);
		Writer out = new StringWriter();

		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(document);

		return out.toString();
	}
	
	public static void main(String[] args) throws Exception {
		ExtensionProcessor processor = new ExtensionProcessor();
		Element root = processor.processResource("schema/extension.xml");
		System.out.println(format(root));
	}

}
