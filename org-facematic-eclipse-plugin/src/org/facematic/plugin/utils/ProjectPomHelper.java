package org.facematic.plugin.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.facematic.utils.StreamUtils;
import static org.facematic.Settings.*;

/**
 * Helper for work with project POM file
 * 
 * @author stas
 *
 */
public class ProjectPomHelper {
	
	private Document document;
	private String groupId;
	private String artifactId;
	private String version;
	private Element root;
	private Element dependencies;
	private Element repositories;
	private Element properties;
	private String  source = "";

	private void build (Document document) {
		this.document = document;
		root = document.getRootElement();
		
		Element parent = root.element ("parent");
		 
		
		
		Element groupId = root.element("groupId");
		if (groupId == null) {
			if (parent != null)
				groupId = parent.element ("groupId");
		}
		this.groupId = groupId.getText();
		
		Element artifactId = root.element("artifactId");
		this.artifactId = artifactId.getText();
		
		Element eElement = root.element ("version");
		if (eElement==null) { 
			if (parent!= null)
				eElement = parent.element ("version");
		}
			
		this.version = eElement.getText();
		
		dependencies = root.element("dependencies");
		if (dependencies==null) {
			dependencies = root.addElement("dependencies");
		}
		
		repositories = root.element("repositories");
		if (repositories==null) {
			repositories = root.addElement("repositories");
		}
		
		properties = root.element("properties");
		if (properties==null) {
			properties = root.addElement("properties");
		}
	}
	
	public ProjectPomHelper  (String source) throws DocumentException {
		this.source = source;
		build (DocumentHelper.parseText(source));
	}
	
	/**
	 * @param in
	 * @throws DocumentException
	 * @throws IOException
	 */
	public ProjectPomHelper (InputStream in) throws DocumentException, IOException {
		this (StreamUtils.getString(in));
	}
	
	/**
	 * returns true if facematic is already implemented
	 * @return
	 */
	public boolean isFacematic () {
		Element packaging = root.element ("packaging");
		if (packaging==null) return false;
		if (!"war".equals(packaging.getText())) return false;
		
		Element dependencies = root.element("dependencies");
		if (dependencies==null) {
			return false;
		}
		if (!haveDependency (dependencies, "org.facematic", "facematic")) return false;
		if (!haveDependency (dependencies, "com.vaadin", "vaadin-client-compiled")) return false;
		if (!haveDependency (dependencies, "com.vaadin", "vaadin-themes")) return false;
		return true;
	}
	
	/**
	 * returns true if facematic is already implemented
	 * @return
	 */
	public boolean isWar () {
		Element packaging = root.element ("packaging");
		if (packaging==null) return false;
		return "war".equals(packaging.getText());
	}
	
	/**
	 * adds facematic features to project (modifies pom dom) 
	 */
	public void addFacematicFeatures() {
		if (isFacematic()) {
			return;
		}
		
		Element packaging = root.element ("packaging");
		if (packaging==null) {
			root.addElement("packaging").setText("war");
		}
		
		addDependency    (dependencies, "org.facematic", "facematic", "${facematic.version}", "compile");
		addDependency    (dependencies, "com.vaadin", "vaadin-client-compiled", "${vaadin.version}", null);
		addDependency    (dependencies, "com.vaadin", "vaadin-themes", "${vaadin.version}", null);
		addDependency    (dependencies, "com.vaadin", "vaadin-theme-compiler", "${vaadin.version}", null);
		addDependency    (dependencies, "javax.servlet", "javax.servlet-api", SERVLET_API_VERSION, "provided");
		addDependency    (dependencies, "javax.enterprise", "cdi-api", CDI_VERSION, "provided");
		
		updateRepository (repositories, "facematic", FACEMATIC_REPOSITORY);
		updateProperty   (properties,   "vaadin.version", VAADIN_VERSION);
		updateProperty   (properties,   "facematic.version", FACEMATIC_VERSION);
		
		this.source = buildXml (); 
	}
	
	/**
	 * returns artifact version
	 * @return
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * returns artifact id
	 * @return
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * returns artifact group id
	 * @return
	 */
	public String getGroupId() {
		return this.groupId;
	}
		
	
	/**
	 * returns pom xml markup
	 * @return
	 */
	private String buildXml () {
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		StringWriter swriter = new StringWriter();
//		XMLWriter writer = new XMLWriter(swriter, format);
//		try {
//			writer.write(document);
//			writer.flush();
//			return swriter.getBuffer().toString();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "";
		return document.asXML();
	}
	
	
	public String getSource () {
		return source;
	}
	
	/**
	 * Checks, if the project have specified groupId:artifactId dependency
	 * @param dependencies
	 * @param groupId
	 * @param artifactId
	 * @return
	 */
	private boolean haveDependency (Element dependencies, String groupId, String artifactId) {
		for (Element e : (List<Element>)dependencies.elements()) {
			if (e.element("groupId") == null) continue;
			if (e.element("artifactId") == null) continue;
			if (groupId.equals(e.element("groupId").getText()))
				if (artifactId.equals(e.element("artifactId").getText()))
					return true;
		}
		return false;
	} 

	
	/**
	 * updates specified pom property
	 * @param properties
	 * @param propertyName
	 * @param propertyValue
	 */
	private void updateProperty(Element properties, String propertyName, String propertyValue) {
		for (Element e : (List<Element>)properties.elements()) {
			if (propertyName.equals (e.getName())) {
				properties.remove(e);
				break;
			}
		}
		
		properties.addElement(propertyName).setText(propertyValue);
	}

	/**
	 * Updates repository URL to the actual location value
	 * 
	 * @param repositories
	 * @param id
	 * @param url
	 */
	private void updateRepository(Element repositories, String id, String url) {
		for (Element e : (List<Element>)repositories.elements()) {
			if (e.element("id") == null) continue;
			if (id.equals(e.element("id").getText())) {
				repositories.remove(e);
				break;
			}
		}
		Element repository = repositories.addElement("repository");
		repository.addElement("id").setText(id);
		repository.addElement("url").setText(url);
		
	}

	/**
	 * adds dependency, if such groupId:artifactId does not exist
	 * @param dependencies
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param scope
	 */
	private void addDependency(Element dependencies, String groupId, String artifactId, String version, String scope) {
		for (Element e : (List<Element>)dependencies.elements()) {
			if (e.element("groupId") == null) continue;
			if (e.element("artifactId") == null) continue;
			if (groupId.equals(e.element("groupId").getText()))
				if (artifactId.equals(e.element("artifactId").getText()))
					return;
		}
		createDependency (dependencies, groupId, artifactId, version, scope);
	}

	
	
	/**
	 * creates dependency
	 * 
	 * @param dependencies
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param scope
	 */
	private void createDependency (Element dependencies, String groupId, String artifactId, String version, String scope) {
		Element dependency = dependencies.addElement("dependency");
		dependency.addElement("groupId").setText(groupId);
		dependency.addElement("artifactId").setText(artifactId);
		if (version != null) {
		    dependency.addElement("version").setText(version);
		}
		if (scope != null) {
			dependency.addElement("scope").setText(scope);
		}
		
	}
	


}
