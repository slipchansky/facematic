package org.facematic.core.producer;

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.facematic.core.producer.builders.AbstractOrderedLayoutBuilder;
import org.facematic.core.producer.builders.BeanBuilder;
import org.facematic.core.producer.builders.ButtonBuilder;
import org.facematic.core.producer.builders.CompositeBuilder;
import org.facematic.core.producer.builders.ComponentBuilder;
import org.facematic.core.producer.builders.ComponentContainerBuilder;
import org.facematic.core.producer.builders.HtmlBuilder;
import org.facematic.core.producer.builders.NvoItemBuilder;
import org.facematic.core.producer.builders.PanelBuilder;
import org.facematic.core.producer.builders.SelectBuilder;
import org.facematic.core.producer.builders.TabSheetBuilder;
import org.facematic.core.producer.builders.TableBuilder;
import org.facematic.core.producer.builders.TreeBuilder;
import org.facematic.core.producer.builders.UploadBuilder;
import org.facematic.core.annotations.FmView;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.nvo.Item;
import org.facematic.core.ui.DummyFacematicUi;
import org.facematic.core.ui.FacematicUI;
import org.facematic.core.ui.custom.Composite;
import org.facematic.core.ui.custom.Html;
import org.facematic.utils.ITemplateEngine;
import org.facematic.utils.VelocityEngine;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggerRepository;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author "Stanislav Lipchansky"
 * 
 */
public class FaceProducer implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(FaceProducer.class);

	public interface NodeWatcher {
		void putView(String name, Object view);

		void putController(String name, Object controller);

		void setListener(String name, Class parameterType, Class producerClass,
				String producerName, String producerCaption, String eventName);
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
			putController = clazz.getMethod("putController", String.class,
					Object.class);
			setListener = clazz.getMethod("setListener", String.class,
					Class.class, Class.class, String.class, String.class,
					String.class);
		}

		public void putView(String name, Object view) {
			try {
				putView.invoke(obj, name, view);
			} catch (Exception e) {
				logger.error("Cant put view, name=" + name, e);
			}
		}

		public void putController(String name, Object controller) {
			try {
				putController.invoke(obj, name, controller);
			} catch (Exception e) {
				logger.error("Cant put controller, name=" + name, e);
			}
		}

		@Override
		public void setListener(String name, Class parameterType,
				Class producerClass, String producerName,
				String producerCaption, String eventName) {
			try {
				setListener.invoke(obj, name, parameterType, producerClass,
						producerName, producerCaption, eventName);
			} catch (Exception e) {
				logger.error("Cant set event listener, name=" + name
						+ ", parameterType=" + parameterType + "producerClass="
						+ producerClass + ", producerName=" + producerName
						+ ", producerCaption=" + producerCaption
						+ ", enemtName=" + eventName, e);
			}
		}
	}

	private ClassLoader customClassLoader;
	private String prefix = "";
	private FaceReflectionHelper reflectionHelper;
	private Object controllerInstance;
	private FaceProducer parent;
	private NodeWatcher structureWatcher = null;

	private ITemplateEngine engine;

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
				put("item", Item.class);
			}
		};

		private static Map<Class, BeanBuilder> componentBuilders = new HashMap<Class, BeanBuilder>();

		// Register bean builders
		static {
			putBuilder (new BeanBuilder());
			putBuilder (new ComponentBuilder());
			putBuilder (new ComponentContainerBuilder());
			putBuilder (new TabSheetBuilder());
			putBuilder (new AbstractOrderedLayoutBuilder());
			putBuilder (new ButtonBuilder());
			putBuilder (new SelectBuilder());
			putBuilder (new TableBuilder());
			putBuilder (new PanelBuilder());
			putBuilder (new CompositeBuilder());
			putBuilder (new HtmlBuilder());
			putBuilder (new NvoItemBuilder());
			putBuilder (new TreeBuilder());
			putBuilder (new UploadBuilder());
		};

		private static void putBuilder(BeanBuilder componentBuilder) {
			logger.info("register builder: "
					+ componentBuilder.getClass().getCanonicalName());
			componentBuilders.put(componentBuilder.getBuildingClass(),
					componentBuilder);
		}

		private static BeanBuilder getBuilder(Class clazz) {
			if (clazz == null || clazz == Object.class) {
				return null;
			}
			BeanBuilder builder = componentBuilders.get(clazz);
			if (builder != null) {
				logger.trace("Builder " + builder.getClass().getCanonicalName()
						+ " found for " + clazz.getCanonicalName());
				return builder;
			} 
			return getBuilder(clazz.getSuperclass());
		}
	}

	/**
	 * for plugin purposes
	 */
	public FaceProducer() {
		super();
		if (ui == null) {
			this.ui = new DummyFacematicUi();
			logger.info("producer with dummy ui created");
		}
	}

	/**
	 * UI - based producer
	 * 
	 * @param ui
	 */
	public FaceProducer(FacematicUI ui) {
		if (this.ui == null) {
			if (ui == null) {
				logger.error("ui cannot be null");
				throw new RuntimeException(
						"You cannot create instance of FaceProducer without UI reference");
			}
			this.ui = ui;
		}
	}

	/**
	 * Controller - based hierarchical producer
	 * 
	 * @param controllerInstance
	 * @param parent
	 * @param parentNamePrefix
	 */
	public FaceProducer(Object controllerInstance, FaceProducer parent,
			String parentNamePrefix) {
		if (parent != null) {
			if (this.ui == null) {
				this.ui = parent.getUi();
			}
			this.customClassLoader = parent.customClassLoader;
		}
		if (ui == null) {
			logger.error("ui cannot be null");
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}

		this.controllerInstance = controllerInstance;
		this.prefix = parentNamePrefix;
		this.parent = parent;
	}

	/**
	 * Controller based producer
	 * 
	 * @param controllerInstance
	 * @param ui
	 */
	public FaceProducer(Object controllerInstance, FacematicUI ui) {
		this.controllerInstance = controllerInstance;
		if (this.ui == null) {
			this.ui = ui;
		}
		if (ui == null) {
			logger.error("ui cannot be null");
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}
	}

	/**
	 * For hierarchical building purposes
	 * 
	 * @return
	 */
	private FacematicUI getUi() {
		return ui;
	}


	/**
	 * Builds view-controller pair by given entire xml document
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private <T> T build(Document document) throws Exception {
		try {
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
		} catch (Exception e) {
			logger.error("Component building throws exception", e);
			throw e;
		}
	}

	private void prepareBuildingEnvironment(Element root) {
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
	 * 
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
	 * 
	 * @param root
	 */
	private void createControllerInstance(Element root) {
		String controllerClassName = root.attributeValue("controller");
		if (controllerClassName == null) {
			logger.trace("undefined controller class. we will create view without controller.");
			return;
		}
		try {
			controllerInstance = createClassInstance(controllerClassName);
			if (controllerInstance != null) {
				if (controllerInstance instanceof FmBaseController) {
					try {
					((FmBaseController)controllerInstance).prepareContext(this);
					} catch (Exception e) {
						logger.error("Could not prepare context. If you see this message in Facematic console, it is probably due to the fact that your code is not running in a work context. In this case, you can ignore this message.", e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Can't instantinate controller: "
					+ controllerClassName, e);
		}
	}
	
	/**
	 * Gets view for specified controller class 
	 * @param controllerClass
	 * @return
	 * @throws Exception
	 */
	public <T> T getViewFor (Class controllerClass) throws Exception {
		String viewName = null;
		try {
		   FmView viewAnnotation = (FmView) controllerClass.getAnnotation(FmView.class);
		   if (viewAnnotation != null) {
			   viewName = viewAnnotation.name();
		   }
		   
		} catch (Exception e) {
			// skip;
		}
		if (viewName==null) {
			logger.warn("Class "+controllerClass.getCanonicalName()+" does not contain valid view name.");
			viewName = controllerClass.getCanonicalName();
			viewName = updateSuffix(viewName, "View", "Controller");
			viewName = viewName.replace(".controllers.", ".views.");
			logger.warn("Try to get fiew from " + viewName);
		}
		return buildFromResource(viewName);
	}
	
	
	/**
	 * Gets view for assigned controller
	 * @see FaceProducer#FaceProducer(Object, FacematicUI)
	 * @see FaceProducer#FaceProducer(Object, FaceProducer, String)
	 * 
	 * @return
	 * @throws Exception
	 */
	public <T> T getView () throws Exception {
		if (controllerInstance==null) {
			logger.error("Controller instance does not exists.");
			return null;
		}
		return getViewFor (controllerInstance.getClass());
	}

	private String updateSuffix(String original, final String validSuffix,
			final String invalidSuffix) {
		if (original.endsWith(invalidSuffix)) {
			original = original.substring(0,
					original.length() - invalidSuffix.length()) + validSuffix;
		}
		return original;
	}
	
	/**
	 * Build view-controller by xml description, saved as application resource
	 * 
	 * @param resourceName
	 *            - qualified name | path-name of resouce, for example:
	 *            org.some.Resource | org/some/Resource.xml
	 * @return
	 * @throws Exception
	 */
	public <T> T buildFromResource(String resourceName) throws Exception {
		logger.info("Build from resource " + resourceName);
		String xml = org.facematic.utils.StreamUtils
				.getResourceAsString(resourceName);
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
		return (T) buildFromString(xml);
	}

	/**
	 * Build view-controller by xml text
	 * 
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
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public <T> T build(Element node) throws Exception {
		Object viewInstance = createInstance(node);

		if (viewInstance == null) {
			logger.error("Instance haven't been created, node=" + node);
			return null;
		}

		String itemname = node.attributeValue("name");
		if ((itemname != null && !"".equals(itemname))
				&& viewInstance instanceof Component) {
			logger.trace("Add view "+viewInstance.getClass().getCanonicalName()+", name=" + itemname);
			addView(itemname, (Component) viewInstance);
		}

		BeanBuilder builder = BeanBuilderFactory.getBuilder(viewInstance.getClass());
		if (builder == null) {
			logger.trace("Builder not found for " + viewInstance.getClass()
					+ ", use as is. name=" + itemname);
			return (T) viewInstance;
		}

		try {
			builder.build(this, viewInstance, node);
			logger.trace("Instance of "+viewInstance.getClass().getCanonicalName()+" succesfully built by "+builder.getClass().getCanonicalName());
		} catch (Exception e) {
			logger.warn("Insuccessfull instance building, use as is, name="
					+ itemname);
		}

		return (T) viewInstance;
	}

	/**
	 * Creates instance according to given xml node
	 * 
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
		logger.trace("Try to create instance of " + name);

		String substName = substs.get(name.toLowerCase());
		if (substName != null) {
			logger.trace("Creation of " + name + ", class name substituted: "
					+ substName);
			name = substName;
		}

		String className = node.attributeValue("class");
		if (className != null) {
			logger.trace("Node contains custom class name: " + name);
			return createClassInstance(className);
		}

		Class clazz = BeanBuilderFactory.CREATORS.get(name);

		if (clazz != null) {
			logger.trace("Class creator= " + clazz.getCanonicalName());
			return clazz.newInstance();
		}
		return createClassInstance(name);
	}

	/**
	 * Creates instance of class by name
	 * 
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
			logger.trace("Found class " + className);
		} catch (Exception e) {
		}

		if (clazz == null)
			try {
				clazz = Class.forName("com.vaadin.ui." + className);
				logger.trace("Found class " + "com.vaadin.ui." + className);
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
				try {
					Object instance = ((FacematicUI) ui)
							.getClassInstance(clazz);
					if (instance != null) {
						logger.trace("CDI environment successfuly instantinated "
								+ clazz.getCanonicalName());
						return (T) instance;
					}

				} catch (Exception e) {
					logger.info("Couldn't get bean instance by BeanManager");
				}
			}
		}

		logger.trace("Try to get newInstance of " + clazz.getCanonicalName());
		try {
			Object instance = clazz.newInstance();
			logger.trace("Successfuly instantinated " + clazz.getCanonicalName());
			return (T) instance;
		} catch (Exception e) {
			logger.error("Can't instantinate " + clazz);
			throw (new RuntimeException (e));
		}
		
		
		
	}

	/**
	 * Load class by external classloader (for plugin purposes)
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class loadClass(String className) throws ClassNotFoundException {
		if (this.customClassLoader == null) {
			return null;
		}
		try {
			return customClassLoader.loadClass(className);

		} catch (ClassNotFoundException e) {
			logger.trace("Could not get class " + className
					+ " by custom classloader");
			throw (e);
		}
	}

	/**
	 * Groovy Engine instance
	 * 
	 * @return
	 */
	public ITemplateEngine getTemplateEngine() {
		if (this.engine == null) {
			this.engine = new VelocityEngine();
		}
		return engine;
	}

	/**
	 * adds view instance
	 * 
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
	 * 
	 * @param name
	 * @param parameterType
	 * @param value
	 */
	public void setListener(String name, Class parameterType,
			Class producerClass, String producerName, String producerCaption,
			String eventName) {
		if (structureWatcher != null) {
			structureWatcher.setListener(name, parameterType, producerClass,
					producerName, producerCaption, eventName);
		}
	}

	/**
	 * Adds structure watcher for trace hierarchical dependencies
	 * 
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
	 * 
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

	/**
	 * Add named variable to context
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		getTemplateEngine().put(key, value);
	}
	
	/**
	 * Add contents of map to context 
	 * @param map
	 */
	public void putAll(Map map) {
		getTemplateEngine().put(map);
	}
}
