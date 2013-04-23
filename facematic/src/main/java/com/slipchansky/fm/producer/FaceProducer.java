package com.slipchansky.fm.producer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
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
import com.slipchansky.fm.mvc.FmBaseController;
import com.slipchansky.fm.ui.Composite;
import com.slipchansky.fm.ui.Html;
import com.slipchansky.utils.GroovyEngine;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;



/**
 * @author stas
 *
 */
public class FaceProducer {
	private String prefix = "";
	private FaceReflectionHelper reflectionHelper;
	private GroovyEngine engine;
	private Object       controllerInstance;
	private FaceProducer parent;
	private Object     context;
	private StructureListener structureListener = null;
	private UI ui;
	
	private final static Map<String, String> substs = new HashMap<String, String> () {{
		put ("vl", VerticalLayout.class.getSimpleName());
		put ("hl", HorizontalLayout.class.getSimpleName());
		put ("content", VerticalLayout.class.getSimpleName());
	}};
	
	
	static class BeanFactory {
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
	
	private static void putBuilder(BeanBuilder componentBuilder) {
		BeanFactory.componentBuilders.put (componentBuilder.getBuildingClass(), componentBuilder);
	}
	
	public FaceProducer(UI ui) {
		if (ui==null) {
			throw new RuntimeException ("You cannot create instance of FaceProducer without UI reference");
		}
		this.ui = ui;
	}
	
	
	public FaceProducer(Object controllerInstance, FaceProducer parent, String name) {
		if (parent != null) {
			this.ui = parent.getUi();
		}
		if (ui==null) {
			throw new RuntimeException ("You cannot create instance of FaceProducer without UI reference");
		}
		
		this.controllerInstance = controllerInstance;
		this.prefix = name;
		this.parent = parent;
	}
	
	public FaceProducer(Object controllerInstance, UI ui) {
		this.controllerInstance = controllerInstance;
		this.ui = ui;
		if (ui==null) {
			throw new RuntimeException ("You cannot create instance of FaceProducer without UI reference");
		}
	}

	private UI getUi() {
		return ui;
	}

	public void setContext (Object context) {
		this.context = context;
	} 
	
	private <T>T build (Document document) throws Exception {
		Element root = document.getRootElement();
		prepareContext (root);
		Object view = build (root);
		if (view instanceof Component) {
		    putView ("view", (Component)view);
		}
		if (controllerInstance != null) {
			if (controllerInstance instanceof FmBaseController) {
				((FmBaseController)controllerInstance).init();
			}
		}
		return (T)view;
	}
	
	private void prepareContext(Element root) {
		if ( controllerInstance == null ) {
			prepareControllerInstance (root);
		}
		reflectionHelper = new FaceReflectionHelper (controllerInstance);
		reflectionHelper.addUiInjections (ui);
		
		if (parent != null && controllerInstance != null && prefix != null) {
			parent.putController(prefix, controllerInstance);
			putController ("parent", parent.getControllerInstance());
		}
	}

	private void putController(String name, Object controllerInstance) {
		if (structureListener != null) {
			structureListener.putController(name, controllerInstance);
		}
		
		reflectionHelper.putController (name, controllerInstance);
	}

	private void prepareControllerInstance(Element root) {
		String controllerClassName = root.attributeValue("controller");
		if (controllerClassName==null) {
			return;
		}
		try {
			controllerInstance = createClassInstance(controllerClassName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T>T buildFromResource (String resourceName) throws Exception {
		String xml = com.slipchansky.utils.StreamUtils.getResourceAsString(resourceName);
		if (xml==null) {
			resourceName = resourceName.replaceAll("\\.", "/")+".xml";
			xml = com.slipchansky.utils.StreamUtils.getResourceAsString(resourceName);
		}
		
		if (xml==null) {
			return null;
		}
		return (T)buildFromString (xml);
	} 
	
	public <T>T build(Element node) throws Exception {
		Object viewInstance = createInstance (node);
		
		if (viewInstance == null) {
			return null;
		}
		
		
		
		String itemname = node.attributeValue ("name");
		if ( (itemname != null && !"".equals(itemname)) && viewInstance instanceof Component) {
	         putView (itemname, (Component)viewInstance);
		}
		
		BeanBuilder builder = BeanFactory.getBuilder(viewInstance.getClass ());
		if (builder==null) {
			return (T)viewInstance;
		}
		
		builder.build(this, viewInstance, node);
		return (T)viewInstance;
		
	}

	private Object createInstance(Element node) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException {
		String name = node.getName();
		String substName = substs.get(name.toLowerCase());
		if (substName != null) {
			name = substName;
		}
		
		
		
		String className = node.attributeValue("class");
		if (className != null) {
			return createClassInstance(className);
		}
		
		Class clazz = BeanFactory.CREATORS.get(name);
		
		if (clazz != null) {
			return clazz.newInstance();
		}
		
		
		return createClassInstance (name);
	}
	
	public <T> T createComplexClasInstance (Element node) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (T) new Composite();
	}
	

	public <T>T createClassInstance (String componentClassSimpleName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException {
		
		try {
			return (T)Class.forName(componentClassSimpleName).newInstance();
		} catch (Exception e) {
			return (T)Class.forName("com.vaadin.ui."+componentClassSimpleName).newInstance();
		}
	}
	
	public GroovyEngine getGroovyEngine() {
		if (this.engine==null) {
			this.engine = new GroovyEngine();
			engine.put ("context", context);
		}
		return engine;
	}


	public void putView(String name, Component value)  {
		if (structureListener != null) {
		    structureListener.putView(name, value);
		}
		
		reflectionHelper.putView(name, value);
		
		if (parent != null) {
			parent.putView(prefix+'.'+name, value);
		}
	}

	
	public <T>T buildFromString(String xml) throws Exception {
		if (xml == null) {
			return null;
		}
		Document document = DocumentHelper.parseText(xml);
		return (T)build (document);
	}


	public <T> T getControllerInstance() {
		return (T)controllerInstance;
	}
	
	public void setStructureListener (StructureListener structureListener) {
		this.structureListener = structureListener;
	}
	
	

		

}
