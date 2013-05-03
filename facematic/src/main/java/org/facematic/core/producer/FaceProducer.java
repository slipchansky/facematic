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
import org.facematic.core.ui.Composite;
import org.facematic.core.ui.DummyFacematicUi;
import org.facematic.core.ui.Html;
import org.facematic.utils.GroovyEngine;
import org.facematic.utils.StreamUtils;

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
public class FaceProducer implements Serializable {

	public interface NodeWatcher {
		void putView(String name, Object view);
		void putController(String name, Object controller);
	}
	class NodeWatcherProxy implements NodeWatcher {
		private Object obj;
		Method putView;
		Method putController;

		public NodeWatcherProxy (Object obj) throws NoSuchMethodException, SecurityException {
			this.obj = obj;
			Class clazz = obj.getClass();
			putView = clazz.getMethod("putView", String.class, Object.class);
			putController = clazz.getMethod("putController", String.class, Object.class);
			
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
	}
	

	private ClassLoader customClassLoader;
	private String prefix = "";
	private FaceReflectionHelper reflectionHelper;
	private GroovyEngine engine;
	private Object controllerInstance;
	private FaceProducer parent;
	private Object context;
	private NodeWatcher structureWatcher = null;
	private UI ui;

	private final static Map<String, String> substs = new HashMap<String, String>() {
		{
			put("vl", VerticalLayout.class.getSimpleName());
			put("hl", HorizontalLayout.class.getSimpleName());
			put("content", VerticalLayout.class.getSimpleName());
		}
	};

	static class BeanFactory {
		final static Map<String, Class> CREATORS = new HashMap<String, Class>() {
			{
				put("composite", Composite.class);
				put("html", Html.class);
				put("Html", Html.class);
			}
		};
		private static Map<Class, BeanBuilder> componentBuilders = new HashMap<Class, BeanBuilder>();
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

		private static BeanBuilder getBuilder(Class clazz) {
			if (clazz == null)
				return null;
			BeanBuilder builder = componentBuilders.get(clazz);
			if (builder != null)
				return builder;
			return getBuilder(clazz.getSuperclass());
		}
	}

	private static void putBuilder(BeanBuilder componentBuilder) {
		BeanFactory.componentBuilders.put(componentBuilder.getBuildingClass(),
				componentBuilder);
	}

	/**
	 * for plugin purposes
	 */
	public FaceProducer() {
		super();
		this.ui = new DummyFacematicUi();
	}

	public FaceProducer(UI ui) {
		if (ui == null) {
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}
		this.ui = ui;
	}

	public FaceProducer(Object controllerInstance, FaceProducer parent,
			String name) {
		if (parent != null) {
			this.ui = parent.getUi();
			this.customClassLoader = parent.customClassLoader;
		}
		if (ui == null) {
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}

		this.controllerInstance = controllerInstance;
		this.prefix = name;
		this.parent = parent;
	}

	public FaceProducer(Object controllerInstance, UI ui) {
		this.controllerInstance = controllerInstance;
		this.ui = ui;
		if (ui == null) {
			throw new RuntimeException(
					"You cannot create instance of FaceProducer without UI reference");
		}
	}

	private UI getUi() {
		return ui;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	private <T> T build(Document document) throws Exception {
		Element root = document.getRootElement();
		prepareContext(root);
		Object view = build(root);
		if (view instanceof Component) {
			putView("view", (Component) view);
		}
		if (controllerInstance != null) {
			if (controllerInstance instanceof FmBaseController) {
				((FmBaseController) controllerInstance).init();
			}
		}
		return (T) view;
	}

	private void prepareContext(Element root) {
		if (controllerInstance == null) {
			prepareControllerInstance(root);
		}
		reflectionHelper = new FaceReflectionHelper(controllerInstance);
		reflectionHelper.addUiInjections(ui);

		if (parent != null && controllerInstance != null && prefix != null) {
			parent.putController(prefix, controllerInstance);
			putController("parent", parent.getControllerInstance());
		}
	}

	private void putController(String name, Object controllerInstance) {
		if (structureWatcher != null) {
			structureWatcher.putController(name, controllerInstance);
		}

		reflectionHelper.putController(name, controllerInstance);
	}

	private void prepareControllerInstance(Element root) {
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

	public <T> T build(Element node) throws Exception {
		Object viewInstance = createInstance(node);

		if (viewInstance == null) {
			return null;
		}

		String itemname = node.attributeValue("name");
		if ((itemname != null && !"".equals(itemname))
				&& viewInstance instanceof Component) {
			putView(itemname, (Component) viewInstance);
		}

		BeanBuilder builder = BeanFactory.getBuilder(viewInstance.getClass());
		if (builder == null) {
			return (T) viewInstance;
		}

		builder.build(this, viewInstance, node);
		return (T) viewInstance;

	}

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

		Class clazz = BeanFactory.CREATORS.get(name);

		if (clazz != null) {
			return clazz.newInstance();
		}

		return createClassInstance(name);
	}

	public <T> T createComplexClasInstance(Element node)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return (T) new Composite();
	}

	@SuppressWarnings("unchecked")
	public <T> T createClassInstance(String componentClassSimpleName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException {

		T result = null;

		if (result == null)
			try {
				result = (T) Class.forName(componentClassSimpleName)
						.newInstance();
			} catch (Exception e) {
			}
		
		if (result == null)
			try {
				result = (T) Class.forName(
						"com.vaadin.ui." + componentClassSimpleName).newInstance();
			} catch (Exception e) {

			}

		if (result == null)
			try {
				result = (T) loadClass(componentClassSimpleName).newInstance();
			} catch (Exception e2) {
			}

		if (result == null)
			try {
				result = (T) loadClass(
						"com.vaadin.ui." + componentClassSimpleName)
						.newInstance();
			} catch (Exception e1) {
			}

		return result;
	}

	private Class loadClass(String className) throws ClassNotFoundException {
		if (this.customClassLoader == null)
			return null;
		return customClassLoader.loadClass(className);
	}

	public GroovyEngine getGroovyEngine() {
		if (this.engine == null) {
			this.engine = new GroovyEngine();
			engine.put("context", context);
		}
		return engine;
	}

	public void putView(String name, Component value) {
		if (structureWatcher != null) {
			structureWatcher.putView(name, value);
		}

		reflectionHelper.putView(name, value);

		if (parent != null) {
			parent.putView(prefix + '.' + name, value);
		}
	}

	public <T> T buildFromString(String xml) throws Exception {
		if (xml == null) {
			return null;
		}
		Document document = DocumentHelper.parseText(xml);
		return (T) build(document);
	}

	public <T> T getControllerInstance() {
		return (T) controllerInstance;
	}

	public void setStructureWatcher(final Object watcher) {
		  try{
		  this.structureWatcher = (NodeWatcher)watcher;
		  } catch (Exception e) {
			  try {
				this.structureWatcher = new NodeWatcherProxy (watcher);
			} catch (Exception e1) {
			}
		  }
	}

	public void setCustomClassLoader(ClassLoader customClassLoader) {
		this.customClassLoader = customClassLoader;
	}

	public void setUi(Object ui) {
		this.ui = (UI) ui;
	}

}
