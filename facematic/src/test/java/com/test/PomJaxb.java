package com.test;


import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "project", namespace="http://maven.apache.org/POM/4.0.0")
public class PomJaxb {
	
	String groupId;
	
	String artifactId;
	
	
	public PomJaxb () {
	}
	
	public String getGroupId() {
		return groupId;
	}


	@XmlElement(name="groupId", namespace="http://maven.apache.org/POM/4.0.0")
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	@XmlElement(name="artifactId", namespace="http://maven.apache.org/POM/4.0.0")
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}





	public static void main(String[] args) throws JAXBException {
		File file = new File("Z:/workspace/facematic/facematic/pom.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(PomJaxb.class);
 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		PomJaxb pom = (PomJaxb) jaxbUnmarshaller.unmarshal(file);
		
		int k = 0;
		k++;
		
		
	}
	
	

}
