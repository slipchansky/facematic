package com.slipchansky.vabuilder.tests;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.slipchansky.fm.factory.ComponentFactory;

@Ignore
public class TestBuilder {
	
	ComponentFactory builder;
	
	@Before
	public void init () {
		builder = new ComponentFactory ();
	}

	@Test
	public void testCreateBasicInstance () throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException, DocumentException, IllegalArgumentException, InvocationTargetException {
		 String text = "<Button id=\"1\" value=\"mama\"> <name>James</name> </Button>";
	     Document document = DocumentHelper.parseText(text);
	     ComponentFactory ui = new ComponentFactory ();
		 Object instance = ui.build (document);
		 assertNotNull(instance);
	}
	
	@Test
	public void testCreateInstance () throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException, DocumentException, IllegalArgumentException, InvocationTargetException {
		
		 String text = "<custom class=\"com.vaadin.ui.Button\" id=\"1\" value=\"mama\"> <name>James</name> </custom>";
	     Document document = DocumentHelper.parseText(text);
	     ComponentFactory ui = new ComponentFactory ();
		 Object instance = ui.build (document);
		 assertNotNull(instance);
	}
	

}
