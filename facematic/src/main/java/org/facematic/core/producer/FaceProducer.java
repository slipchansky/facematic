package org.facematic.core.producer;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


import org.facematic.core.producer.builders.AbstractOrderedLayoutBuilder;
import org.facematic.core.producer.builders.BeanBuilder;
import org.facematic.core.producer.builders.ButtonBuilder;
import org.facematic.core.producer.builders.CompositeBuilder;
import org.facematic.core.producer.builders.ComponentBuilder;
import org.facematic.core.producer.builders.ComponentContainerBuilder;
import org.facematic.core.producer.builders.HtmlBuilder;
import org.facematic.core.producer.builders.PanelBuilder;
import org.facematic.core.producer.builders.SelectBuilder;
import org.facematic.core.producer.builders.TabSheetBuilder;
import org.facematic.core.producer.builders.TableBuilder;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.ui.DummyFacematicUi;
import org.facematic.core.ui.FacematicUI;
import org.facematic.core.ui.custom.Composite;
import org.facematic.core.ui.custom.Html;
import org.facematic.utils.GroovyEngine;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class FaceProducer implements Serializable {

	public interface NodeWatcher {
		void putView(String name, Object view);
		void putController(String name, Object controller);
		void setListener(String name, Class parameterType, Class producerClass, String producerName, String producerCaption, String eventName);
	}

	// TODO replace it with standard Proxy !!!
	class NodeWatcherProxy implements NodeWatcher {
		private Object obj;
		Method putView;
		Method putController;
		Method setListener;

		public NodeWatcherProxy(Object obj) throws NoSuchMethodException,
				SecurityException {
			this.obj = obj;
			Class clazz = obj.getClass();
			putView = clazz.getMethod("putView", String.class, Object.class);
			putController = clazz.getMethod("putController", String.class, Object.class);
			setListener= clazz.getMethod("setListener", String.class, Class.class, Class.class, String.class, String.class, String.class);
		}

		public void putView(String name, Object view) {
			try {
				putView.invoke(obj, name, view);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void putController(String name, Object controller) {
			try {
				putController.invoke(obj, name, controller);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		@Override
		public void setListener(String name, Class parameterType, Class producerClass, String producerName, String producerCaption, String eventName) {
			try {
				setListener.invoke(obj, name, parameterType, producerClass, producerName, producerCaption, eventName);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

	private ClassLoader customClassLoader;
	private String prefix = "";
	private FaceReflectionHelper reflectionHelper;
	private Object controllerInstance;
	private FaceProducer parent;
	private NodeWatcher structureWatcher = null;
	
	private GroovyEngine engine;
	private Object context; // groovy execution context
	

	private FacematicUI ui;

	private final static Map<String, String> substs = new HashMap<String, String>() {
		{
			put("vl", VerticalLayout.class.getSimpleName());
			put("hl", HorizontalLayout.class.getSimpleName());
			put("content", VerticalLayout.class.getSimpleName());
		}
	};

	/**
	 * Produces BeanBuilder for specified Vaadin component class by inheritance
	 * hierarchy
	 * 
	 * @author papa
	 */
	static class BeanBuilderFactory {
		final static Map<String, Class> CREATORS = new HashMap<String, Class>() {
			{
				put("composite", Composite.class);
				put("html", Html.class);
				put("Html", Html.class);
			}
		};

		private static Map<Class, BeanBuilder> componentBuilders = new HashMap<Class, BeanBuilder>();

		// Register bean builders
		static {
			putBuilder(new BeanBuilder());
			putBuilder(new ComponentBuilder());
			putBuilder(new ComponentContainerBuilder());
			putBuilder(new TabSheetBuilder());
			putBuilder(new AbstractOrderedLayoutBuilder());
			putBuilder(new ButtonBuilder());
			putBuilder(new SelectBuilder());
			putBuilder(new TableBuilder());
			putBuilder(new PanelBuilder());
			putBuilder(new CompositeBuilder());
			putBuilder(new HtmlBuilder());
		};

		private static void putBuilder(BeanBuilder componentBuilder) {
			componentBuilders.put(componentBuilder.getBuildingClass(),
					componentBuilder);
		}

		private static BeanBuilder getBuilder(Class clazz) {
			if (clazz == null) {
				return null;
			}
			BeanBuilder builder = componentBuilders.get(clazz);
			if (builder != null) {
				return builder;
			}
			return getBuilder(clazz.getSuperclass());
		}
	}

	/**
	 * for plugin purposes
	 */
	public 
	FaceProducer() {
		super();
		if (ui == null) {
			this.ui = new DummyFacematicUi();
		}
	}

	/**
	 * UI - based producer
	 * @param ui
	 */
	public FaceProducer(FacematicUI ui) {
		if (this.ui == null) {
			if (ui == null) {
				throw new RuntimeException(
						"You cannot create instance of FaceProducer without UI reference");
			}
			this.ui = ui;
		}
	}

	/**
	 * Controller - based hierarchical producer
	 * @param controllerInstance
	 * @param parent
	 * @param parentNamePrefix
	 */
	public FaceProducer(Object controllerInstance, FaceProducer parent, String parentNamePrefix) {
		if (parent != null) {
			if (this.ui == null) {
			    this.ui = parent.getUi();
			}
			this.customClassLoader = parent.customClassLoader;
		}
		if (ui == null) {
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}

		this.controllerInstance = controllerInstance;
		this.prefix = parentNamePrefix;
		this.parent = parent;
	}

	/**
	 * Controller based producer
	 * @param controllerInstance
	 * @param ui
	 */
	public FaceProducer(Object controllerInstance, FacematicUI ui) {
		this.controllerInstance = controllerInstance;
		if (this.ui == null) {
		    this.ui = ui;
		}
		if (ui == null) {
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}
	}

	
	/**
	 * For hierarchical building purposes
	 * @return
	 */
	private FacematicUI getUi() {
		return ui;
	}

	/**
	 * Assign
	 * @param context
	 */
	public void setContext(Object context) {
		this.context = context;
	}

	/**
	 * Builds view-controller pair by given entire xml document
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private <T> T build(Document document) throws Exception {
		Element root = document.getRootElement();
		prepareBuildingEnvironment(root);
		Object view = build(root);
		if (view instanceof Component) {
			addView("view", (Component) view);
		}
		if (controllerInstance != null) {
			if (controllerInstance instanceof FmBaseController) {
				((FmBaseController) controllerInstance).init();
			}
		}
		return (T) view;
	}

	private void prepareBuildingEnvironment (Element root) {
		if (controllerInstance == null) {
			createControllerInstance(root);
		}
		reflectionHelper = new FaceReflectionHelper(controllerInstance);
		reflectionHelper.addUiInjections(ui);

		if (parent != null && controllerInstance != null && prefix != null) {
			parent.addController(prefix, controllerInstance);
			addController("parent", parent.controllerInstance);
		}
	}
	

	/**
	 * Add controller instance
	 * @param name
	 * @param controllerInstance
	 */
	private void addController(String name, Object controllerInstance) {
		if (structureWatcher != null) {
			structureWatcher.putController(name, controllerInstance);
		}
		reflectionHelper.putController(name, controllerInstance);
	}

	/**
	 * Creates controller instance
	 * @param root
	 */
	private void createControllerInstance(Element root) {
		String controllerClassName = root.attributeValue("controller");
		if (controllerClassName == null) {
			return;
		}
		try {
			controllerInstance = createClassInstance(controllerClassName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build view-controller by xml description, saved as application resource
	 * @param resourceName - qualified name | path-name of resouce, for example: org.some.Resource | org/some/Resource.xml 
	 * @return
	 * @throws Exception
	 */
	public <T> T buildFromResource(String resourceName) throws Exception {
		String xml = org.facematic.utils.StreamUtils
				.getResourceAsString(resourceName);
		if (xml == null) {
			resourceName = resourceName.replaceAll("\\.", "/") + ".xml";
			xml = org.facematic.utils.StreamUtils
					.getResourceAsString(resourceName);
		}

		if (xml == null) {
			return null;
		}
		return (T) buildFromString(xml);
	}

	/**
	 * Build view-controller by xml text
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public <T> T buildFromString(String xml) throws Exception {
		if (xml == null) {
			return null;
		}
		Document document = DocumentHelper.parseText(xml);
		return (T) build(document);
	}
	
	/**
	 * Build view-controller according to given xml node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public <T> T build(Element node) throws Exception {
		Object viewInstance = createInstance(node);

		if (viewInstance == null) {
			return null;
		}

		String itemname = node.attributeValue("name");
		if ((itemname != null && !"".equals(itemname))
				&& viewInstance instanceof Component) {
			addView(itemname, (Component) viewInstance);
		}

		BeanBuilder builder = BeanBuilderFactory.getBuilder(viewInstance.getClass());
		if (builder == null) {
			return (T) viewInstance;
		}

		builder.build(this, viewInstance, node);
		return (T) viewInstance;

	}

	/**
	 * Creates instance according to given xml node
	 * @param node
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws ClassCastException
	 */
	private Object createInstance(Element node) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException, InstantiationException, ClassCastException {
		String name = node.getName();
		
		String substName = substs.get(name.toLowerCase());
		if (substName != null) {
			name = substName;
		}

		String className = node.attributeValue("class");
		if (className != null) {
			return createClassInstance(className);
		}

		Class clazz = BeanBuilderFactory.CREATORS.get(name);

		if (clazz != null) {
			return clazz.newInstance();
		}
		return createClassInstance(name);
	}


	/**
	 * Creates instance of class by name
	 * @param className
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws ClassCastException
	 */
	@SuppressWarnings("unchecked")
	public <T> T createClassInstance(String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, ClassCastException {

		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (Exception e) {
		}

		if (clazz == null)
			try {
				clazz = Class.forName("com.vaadin.ui."
						+ className);
			} catch (Exception e) {
			}

		if (clazz == null)
			try {
				clazz = loadClass(className);
			} catch (Exception e2) {
			}

		if (clazz == null)
			try {
				clazz = loadClass("com.vaadin.ui." + className);
			} catch (Exception e1) {
			}

		if (clazz == null) {
			return null;
		}

		if (ui != null) {
			if (ui instanceof FacematicUI) {
				Object instance = ((FacematicUI) ui).getClassInstance(clazz);
				if (instance != null) {
					return (T) instance;
				}
			}
		}

		Object instance = clazz.newInstance();
		return (T) instance;
	}

	/**
	 * Load class by external classloader (for plugin purposes) 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class loadClass(String className) throws ClassNotFoundException {
		if (this.customClassLoader == null)
			return null;
		return customClassLoader.loadClass(className);
	}

	/**
	 * Groovy Engine instance
	 * @return
	 */
	public GroovyEngine getGroovyEngine() {
		if (this.engine == null) {
			this.engine = new GroovyEngine();
			engine.put("context", context);
		}
		return engine;
	}

	/**
	 * adds view instance
	 * @param name
	 * @param value
	 */
	public void addView(String name, Component value) {
		if (structureWatcher != null) {
			structureWatcher.putView(name, value);
		}
		reflectionHelper.putView(name, value);
		if (parent != null) {
			parent.addView(prefix + '.' + name, value);
		}
	}
	
	/**
	 * adds view instance
	 * @param name
	 * @param parameterType 
	 * @param value
	 */
	public void setListener(String name, Class parameterType, Class producerClass, String producerName, String producerCaption, String eventName) {
		if (structureWatcher != null) {
			structureWatcher.setListener(name, parameterType, producerClass, producerName, producerCaption, eventName);
		}
	}



	/**
	 * Adds structure watcher for trace hierarchical dependencies  
	 * @param watcher
	 */
	public void setStructureWatcher(final Object watcher) {
		try {
			this.structureWatcher = (NodeWatcher) watcher;
		} catch (Exception e) {
			try {
				this.structureWatcher = new NodeWatcherProxy(watcher);
			} catch (Exception e1) {
			}
		}
	}

	/**
	 * For plugin purposes
	 * @param customClassLoader
	 */
	public void setCustomClassLoader(ClassLoader customClassLoader) {
		this.customClassLoader = customClassLoader;
	}

	/**
	 * @return
	 */
	public Object getControllerInstance() {
		return controllerInstance;
	}
}
