package com.slipchansky.fm.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Button;
import com.slipchansky.fm.factory.builders.AbstractOrderedLayoutBuilder;
import com.slipchansky.fm.factory.builders.BeanBuilder;
import com.slipchansky.fm.factory.builders.ButtonBuilder;
import com.slipchansky.fm.factory.builders.CompositeBuilder;
import com.slipchansky.fm.factory.builders.ComponentBuilder;
import com.slipchansky.fm.factory.builders.ComponentContainerBuilder;
import com.slipchansky.fm.factory.builders.HtmlBuilder;
import com.slipchansky.fm.factory.builders.PanelBuilder;
import com.slipchansky.fm.factory.builders.SelectBuilder;
import com.slipchansky.fm.factory.builders.TabSheetBuilder;
import com.slipchansky.fm.factory.builders.TableBuilder;
import com.slipchansky.fm.ui.Composite;
import com.slipchansky.fm.ui.Html;
import com.slipchansky.utils.GroovyEngine;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;



public class ComponentFactory {
	
	private Document document;
	private Element  rootNode;
	private Map<String, Object> context = new HashMap<String, Object> ();
	private ComponentContainer parentComponent = null;
	private List<String> warnings = new ArrayList<String> ();
	private GroovyEngine engine;
	
	
	
	private final static Map<String, String> substs = new HashMap<String, String> () {{
		put ("vl", VerticalLayout.class.getSimpleName());
		put ("hl", HorizontalLayout.class.getSimpleName());
		put ("content", VerticalLayout.class.getSimpleName());
	}}; 
	
	
	
	
	static class Engine {
		final static Map<String, Class> CREATORS = new HashMap<String, Class> () {{
			put ("composite", Composite.class);
			put ("html", Html.class);
			put ("Html", Html.class);
		}};
		
		
		
		private static Map<Class, BeanBuilder> componentBuilders  = new HashMap<Class, BeanBuilder>  ();
		
		
		static {
			putBuilder (new BeanBuilder ());
			putBuilder (new ComponentBuilder ());
			putBuilder (new ComponentContainerBuilder ());
			putBuilder (new TabSheetBuilder ());
			putBuilder (new AbstractOrderedLayoutBuilder ());
			putBuilder (new ButtonBuilder ());
			putBuilder (new SelectBuilder ());
			putBuilder (new TableBuilder ());
			putBuilder (new PanelBuilder ());
			putBuilder (new CompositeBuilder ());
			putBuilder (new HtmlBuilder ());
		};
		
		
		private static BeanBuilder getBuilder (Class clazz) {
			if (clazz==null)
				return null;
			BeanBuilder builder = componentBuilders.get(clazz);
			if (builder != null ) return builder;
			return getBuilder (clazz.getSuperclass());
		}
		
	}
	
	public ComponentFactory () {
		context.put("context", context);
	}
	
	public ComponentFactory(UI ui) {
		context.put("context", context);
		context.put("applicationUI", ui);
	}

	public static void putBuilder(BeanBuilder componentBuilder) {
		Engine.componentBuilders.put (componentBuilder.getBuildingClass(), componentBuilder);
	}
	
	
	public <T> T build (Document document) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException {
		this.document = document;
		Element root = document.getRootElement();
		AbstractComponent rootComponent = build (root);
		return (T) rootComponent;
	}
	
	public <T> T buildFromResource (String resourceName) throws DocumentException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException {
		String xml = com.slipchansky.utils.StreamUtils.getResourceAsString(resourceName);
		if (xml==null) {
			resourceName = resourceName.replaceAll("\\.", "/")+".xml";
			xml = com.slipchansky.utils.StreamUtils.getResourceAsString(resourceName);
		}
		
		if (xml==null) {
			return null;
		}
		
		context.put ("markupPath", resourceName);
		context.put ("markupLocation", "RESOURCE");
		
		if (xml == null)
			return null;
		
		Document document = DocumentHelper.parseText(xml);
		Object result = build (document);
		
		return (T)result;
	} 
	

	public <T> T build(Element node) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException {
		T instance = (T) createInstance (node);
		if (instance == null) {
			return null;
		}
		
		Attribute nameAttr = node.attribute("name");
		if (nameAttr != null) {
			if (context.get (contextKey(nameAttr)) != null) {
				warnings.add ("Duplicate name: '"+contextKey(nameAttr)+"'");
			}
		   context.put (contextKey(nameAttr), instance);
		}
		
		BeanBuilder builder = Engine.getBuilder(instance.getClass ());
		if (builder==null) {
			return instance;
		}
		
		builder.build(this, instance, node);
		return instance;
	}

	private String contextKey(Attribute nameAttr) {
		String name = nameAttr.getValue();
		return name;
	}
	

	private Object createInstance(Element node) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException {
		String name = node.getName();
		String substName = substs.get(name.toLowerCase());
		if (substName != null) {
			name = substName;
		}
		
		
		
		String className = node.attributeValue("class");
		if (className != null) {
			return Class.forName(className).newInstance();
		}
		
		Class clazz = Engine.CREATORS.get(name);
		
		if (clazz != null) {
			return clazz.newInstance();
		}
		
		
		return createUiClassInstance (name);
	}
	
	public <T> T createComplexClasInstance (Element node) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (T) new Composite();
	}
	

	public <T>T createUiClassInstance (String componentClassSimpleName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException {
		String className = "com.vaadin.ui."+componentClassSimpleName;
		return (T) Class.forName(className).newInstance();
	}
	
	
	
	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public List<String> getWarnings() {
		return warnings;
	}
	
	public <T> T get (String name) {
		String path [] = name.split("\\.");
		int i=0;
		
		Map context = this.context;
		for (; i< path.length-1; i++) {
			String p = path[i];
			Object o = context.get(p);
			if (! (o instanceof Map) ) {
			   return null;
			}
			
			context = (Map)o;
		}
		return (T)context.get(path[i]); 
		
	}



	public GroovyEngine getEngine() {
		if (this.engine==null) {
			this.engine = new GroovyEngine();
			engine.put("context", context);
		}
		return engine;
	}


	public void put(String name, Object person) {
		context.put(name, person);
	}

	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException, DocumentException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
	     ComponentFactory ui = new ComponentFactory ();
	     ui.put("name", "Stas");
	     Object result = ui.buildFromResource("com.test.test");
	     result = ui.buildFromResource("com.test.test");
	     result = ui.buildFromResource("com.test.test");
	     
	     Map<String, Object> context = ui.getContext();
		
		int k =0 ;
		k++;
	}

	

}
