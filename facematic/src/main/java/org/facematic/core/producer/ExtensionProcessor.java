package org.facematic.core.producer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultDocument;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.utils.VelocityEngine;
import org.facematic.utils.XmlUtil;

/**
 * @author "Stanislav Lipchansky"
 * 
 */
public class ExtensionProcessor {

	private static Logger logger = LoggerFactory.getLogger(ExtensionProcessor.class);

	private Map<String, Element> placeholders = new HashMap<String, Element>();
	private Map<String, Object> substitutions = new HashMap();

	private HashMap<String, String> complexResources;

	/**
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public Document processXml(String xml) throws Exception {
		Element result = processString(xml);
		for (String key : placeholders.keySet()) {
			Element e = placeholders.get(key);
			e.getParent().remove(e);
		}
		return new DefaultDocument (implementSubstitutions(result, substitutions));
	}

	public Element processResource(String resourceName) throws Exception {
		String xml = getXmlResource(resourceName);
		Document doc = processXml(xml);
		return doc.getRootElement();
	}

	/**
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Element processFromResource(String resourceName) throws Exception {
		String xml = getXmlResource(resourceName);
		return processString(xml);
	}

	public String getXmlResource(String resourceName) {
		String xml = null;
		
		if (complexResources != null)
		     xml = complexResources.get (resourceName);
		
		if (xml == null)
		    xml = org.facematic.utils.StreamUtils.getResourceAsString(resourceName);
		
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
			loadSubstitutions(root, this.substitutions);
			return root;
		}

		Element zuper = processFromResource(root.attributeValue("base"));
		loadSubstitutions(root, substitutions);
		Element extension = root;
		root = zuper;
		collectPlaceHolders(root);
		for (Element implement : (List<Element>) extension.elements("implement")) {
			String phname = implement.attributeValue("placeholder");
			Element placeholder = placeholders.get(phname);
			if (placeholder == null) {
				logger.warn("Couldn't find placeholder for implementation: "
						+ phname);
				continue;
			}
			Map<String, Object> implementationSubstitutions = new HashMap();
			implementationSubstitutions.putAll(this.substitutions);
			loadSubstitutions(implement, implementationSubstitutions);

			for (Element e : (List<Element>) implement.elements()) {
				e.getParent().remove(e);
				List elements = placeholder.getParent().elements();
				elements.add(elements.indexOf(placeholder), e);
			}
		}

		for (Attribute a : (List<Attribute>) extension.attributes()) {
			if (root.attribute(a.getName()) != null) {
				root.attribute(a.getName()).setValue(a.getValue());
			} else {
				root.addAttribute(a.getName(), a.getValue());
			}
		}

		return root;
	}

	private Element implementSubstitutions(Element element,
			Map<String, Object> substitutions) {
		// if (1==1) return element;
		if (substitutions.size() == 0)
			return element;
		String xml = element.asXML();
		if (xml.indexOf('$') < 0)
			return element;
		VelocityEngine velocity = new VelocityEngine();
		velocity.setBinding(substitutions);
		xml = velocity.translate(xml);
		try {
			Document doc = DocumentHelper.parseText(xml);
			return doc.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
			return element;
		}

	}

	private void loadSubstitutions(Element root,
			Map<String, Object> substitutions) {
		for (Element subst : (List<Element>) root.elements("subst")) {
			String name = subst.attributeValue("name");
			String value = XmlUtil.getInnerContent(subst);
			if (value == null) {
				value = subst.attributeValue("value");
				if (value == null)
					continue;
			}
			substitutions.put(name, value);
		}
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
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(document);
		return out.toString();
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			ExtensionProcessor processor = new ExtensionProcessor();
			Element root = processor.processResource("schema/extension.xml");
		}
		// System.out.println(format(root));
		System.out.println(System.currentTimeMillis() - start);
	}

	public Map<String, Object> getSubstitutions() {
		return substitutions;
	}
	
	public void putSubstitutions (Map<String, Object> substs) {
		if (substs != null)
		substitutions.putAll (substs);
	}

	public void setComplexResources(HashMap<String, String> complexResources) {
		this.complexResources = complexResources;
	}

	
	
}
