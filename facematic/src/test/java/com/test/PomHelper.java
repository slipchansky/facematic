package com.test;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.facematic.utils.StreamUtils;


public class PomHelper {
	
	
	
	private Document document;
	
	public PomHelper (String pomText) throws DocumentException {
		document = DocumentHelper.parseText(pomText);
	}
	
	
	public static void main(String[] args) throws IOException, DocumentException {
		FileInputStream in = new FileInputStream("Z:/workspace/facematic/facematic/pom.xml");
		
		String pomText  = StreamUtils.getString(in);
		in.close ();
		PomHelper helper = new PomHelper (pomText);
		String groupId = helper.getGroupId ();
		helper.getArtefactId ();
		helper.getParentGroupId ();
		helper.getParentArtefactId ();
		
	}


	private void getParentArtefactId() {
		// TODO Auto-generated method stub
		
	}


	private void getParentGroupId() {
		// TODO Auto-generated method stub
		
	}


	private void getArtefactId() {
		// TODO Auto-generated method stub
		
	}
	
	private String getValue (String path) {
		Node node = document.selectSingleNode (path);
		if (node==null) {
			return null;
		}
		return node.getStringValue();
	}


	private String getGroupId() {
		return getValue ("/project/artifactId");
		
	} 

}
