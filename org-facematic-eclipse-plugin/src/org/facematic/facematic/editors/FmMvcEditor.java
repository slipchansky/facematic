package org.facematic.facematic.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;

import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.ide.IDE;
import org.facematic.core.producer.FaceProducer;
import org.facematic.facematic.editors.parts.FmJavaEditor;
import org.facematic.facematic.editors.parts.FmXmlEditor;
import org.facematic.plugin.utils.IJettyServer;
import org.facematic.plugin.utils.JettyUtil;
import org.facematic.plugin.utils.VelocityEvaluator;


@SuppressWarnings({"rawtypes", "unchecked", "restriction"})
class FaceProducerProxy  {
	
	Method createClassInstance;
	Method buildFromString;
	Method setStructureWatcher;
	Object instance;
	
	public FaceProducerProxy (ClassLoader classLoader) throws Exception {
		    Class producerClass = classLoader.loadClass(FaceProducer.class.getCanonicalName());
			instance = producerClass.newInstance();
			createClassInstance = producerClass.getMethod("createClassInstance", String.class);
			buildFromString = producerClass.getMethod("buildFromString", String.class);
			setStructureWatcher = producerClass.getMethod("setStructureWatcher", Object.class);
	}

	public <T> T createClassInstance(String componentClassSimpleName) throws Exception {
		return (T)createClassInstance.invoke(instance, componentClassSimpleName);
	}

	public <T> T buildFromString(String xml) throws Exception {
		return (T)buildFromString.invoke(instance, xml);
	}
	
	public void setStructureWatcher(Object nodeWatcher) throws Exception {
		setStructureWatcher.invoke(instance, nodeWatcher);
	} 
	
	
}

/**
 * <ul>
 * <li>page 0 - xml editor.
 * <li>page 1 - java editor
 * <li>page 2 - browser preview
 * </ul>
 */
/**
 * @author papa
 *
 */
/**
 * @author papa
 *
 */
@SuppressWarnings("restriction")
public class FmMvcEditor extends MultiPageEditorPart implements	IResourceChangeListener {
	final static int   JETTY_PORT_PULL_STARTS_FROM = 8888;
	final static String MAIN_JAVA = "main.java.";
	final static String MAIN_RESOURCES = "main.resources.";
	final static String VIEWS_PACKAGE_SUFFIX = ".views";
	final static String CONTROLLERS_PACKAGE_SUFFIX = ".controllers";
	final static String CONTROLLER_SUFFIX = "Controller";
	final static String VIEW_SUFFIX = "View";
	final static String GENERATED_CODE_BEGIN = "// GENERATED_CODE_BEGIN. Do not replace this comment!";
	final static String GENERATED_CODE_END = "// GENERATED_CODE_END. Do not replace this comment!";
	
	static int jettyPortPull = JETTY_PORT_PULL_STARTS_FROM;
	
	static Map<String, ClassLoader>      classLoadersForProjects  = new ConcurrentHashMap<String, ClassLoader> ();
	static Map<String, IJettyServer> jettyServersForProjects  = new ConcurrentHashMap<String, IJettyServer> ();

	private VelocityEvaluator velocityEvaluator;

	private TextEditor   xmlEditor;
	private FmJavaEditor javaEditor;

	File xmlFile;
	File javaFile;
	private boolean projectStructureIsMaven = false;
	private String  viewPackageName; // com.some.package
	private String  viewName;        // SomeView
	
	private String  controllerPackageName; // com.some.package
	private String  controllerName;        // SomeController
	
	private Project         project;
	
	private boolean          controllerPageMustBeSelectedAfterInit;
	private ClassLoader      projectClassLoader;
	private IJettyServer server;
	private Browser          browser;
	private String webAppPath;

	/**
	 * Creates a multi-page editor example.
	 */
	public FmMvcEditor() {
		super();

	}

	private void createXmlEditor() {
		try {
			xmlEditor = new FmXmlEditor(this);
			FileEditorInput xmlEditorInput = new FileEditorInput(xmlFile);
			int index = addPage(xmlEditor, xmlEditorInput);
			setPageText(index, xmlEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}
	
	private void createBrowser () {
		if (!startJettyForCurrentProject ()) {
			return;
		}
		browser = new Browser(getContainer(), SWT.NONE);
		browser.setUrl ("http://localhost:"+server.getPort()+"/"+viewPackageName+"."+viewName); //server.getPort()
		int index = addPage(browser);
		setPageText(index, "preview");
		
	} 

	private void createJavaEditor() {
		try {
			javaEditor   = new FmJavaEditor(this);
			FileEditorInput javaEditorInput = new FileEditorInput(javaFile);
			int index = addPage(javaEditor, javaEditorInput);
			setPageText(index, javaEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	protected void createPages() {
		createXmlEditor();
		createJavaEditor();
		createBrowser ();
		
		if (controllerPageMustBeSelectedAfterInit) {			
			setActivePage(1);
		} else {
			setActivePage(0);
		}
		
		this.addPageChangedListener(new IPageChangedListener () {
			@Override
			public void pageChanged(PageChangedEvent evt) {
				Object page = evt.getSelectedPage();
				if (page == browser) {
				  if (browser != null) browser.refresh();
				}
			}
		});
		
		
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	@SuppressWarnings("deprecation")
	public void init(IEditorSite site, IEditorInput edInput) throws PartInitException {

		try {
			velocityEvaluator = new VelocityEvaluator();
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

		if (!(edInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}

		
		FileEditorInput editorInput = (FileEditorInput) edInput;
		File file = (File) editorInput.getFile(); 
		controllerPageMustBeSelectedAfterInit = file.getName().endsWith(".java");
		
		Workspace workspace = (Workspace) file.getWorkspace();
		workspace.addResourceChangeListener(this);

		IContainer container = file.getParent(); // /sss/src/main/resources/com/stas/views
		project = (Project)file.getProject(); // /sss
		
	    try {
			prepareProjectClassLoader ();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		String fileName = file.getProjectRelativePath().toString();

		String a[] = container.toString().split("/src/");
		String packageBaseName = a[1].replace("/", ".");
		a = fileName.split("/");
		
		String viewControllerNameBase = a[a.length - 1];
		viewControllerNameBase = updateSuffix (viewControllerNameBase, "", ".xml");
		viewControllerNameBase = updateSuffix (viewControllerNameBase, "", ".java");

		projectStructureIsMaven = packageBaseName.startsWith(MAIN_JAVA);
		packageBaseName = updatePrefix(packageBaseName, MAIN_JAVA);
		packageBaseName = updatePrefix(packageBaseName, MAIN_RESOURCES);

		viewPackageName = updateSuffix(packageBaseName, VIEWS_PACKAGE_SUFFIX, CONTROLLERS_PACKAGE_SUFFIX);
		controllerPackageName = updateSuffix(packageBaseName, CONTROLLERS_PACKAGE_SUFFIX, VIEWS_PACKAGE_SUFFIX);

		viewName = updateSuffix(viewControllerNameBase, VIEW_SUFFIX, CONTROLLER_SUFFIX);
		controllerName = updateSuffix(viewControllerNameBase, CONTROLLER_SUFFIX,VIEW_SUFFIX);

		velocityEvaluator.put("viewPackage", viewPackageName);
		velocityEvaluator.put("controllerPackage", controllerPackageName);
		velocityEvaluator.put("controller", controllerName);
		velocityEvaluator.put("view", viewName);
		velocityEvaluator.put("GENERATED_CODE_BEGIN", GENERATED_CODE_BEGIN);
		velocityEvaluator.put("GENERATED_CODE_END", GENERATED_CODE_END);

		javaFile = updateSourceFile(controllerPackageName, controllerName, MAIN_JAVA, ".java", "controller.vm");
		xmlFile = updateSourceFile(viewPackageName, viewName, MAIN_RESOURCES,".xml", "view.vm");
		this.setTitle(viewControllerNameBase);

		super.init(site, edInput);

	}


	private File updateSourceFile(String packageName, String artefactName,
			String mvnPrefix, String ext, String templateName) {
		String packagePath = packageName;
		if (projectStructureIsMaven) {
			packagePath = "src." + mvnPrefix + packageName;
		} else {
			packagePath = "src." + packageName;
		}

		packagePath = packagePath.replace('.', '/');
		String sourceFileName = packagePath + '/' + artefactName + ext;

		Path javaPath = new Path(sourceFileName);
		File file = (File) project.getFile(javaPath);
		if (!file.exists()) {
			try {
				file.create(prepareSourceCodeByTemplate(templateName), true, null);
				return file;
			} catch (CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		return file;

	}

	private String updateSuffix(String original, final String validSuffix,
			final String invalidSuffix) {
		if (original.endsWith(invalidSuffix)) {
			original = original.substring(0,
					original.length() - invalidSuffix.length()) + validSuffix;
		}
		return original;
	}

	private String updatePrefix(String string, final String prefix) {
		if (string.startsWith(prefix)) {
			string = string.substring(prefix.length());
		}
		return string;
	}

	private InputStream prepareSourceCodeByTemplate (String templateName) {
		try {
			return velocityEvaluator.evaluate(templateName);
		} catch (Exception e) {
			return new ByteArrayInputStream(e.toString().getBytes());
		}
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void doSave(IProgressMonitor monitor) {
		javaEditor.doSave(monitor);
		xmlEditor.doSave(monitor);
	}

	public void doSaveAs() {
		// IEditorPart editor = getEditor(0);
		// editor.doSaveAs();
		// setPageText(0, editor.getTitle());
		// setInput(editor.getEditorInput());
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {

						if (((FileEditorInput) javaEditor.getEditorInput())
								.getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i]
									.findEditor(javaEditor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}

						if (((FileEditorInput) xmlEditor.getEditorInput())
								.getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i]
									.findEditor(xmlEditor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}
	

	public void updateControllerSourceCode() {
		try {
			final  IDocument doc = (IDocument) xmlEditor.getAdapter(IDocument.class);
			String markup = doc.get();
			
			updateControllerJavaSourceCode (markup);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void updateControllerJavaSourceCode (String markup) throws Exception {
		
		FaceProducerProxy producer = new FaceProducerProxy (projectClassLoader); 
		
		FmStructureWatcher watcher = new FmStructureWatcher(controllerName);
		producer.setStructureWatcher(watcher);
		producer.buildFromString (markup);
		
		String controllerJavaSourceCode = watcher.toString();
		
		IDocument doc = javaEditor.getViewer().getDocument();
		
		String existingJavaSourceCode = doc.get();
		
		int startPos = existingJavaSourceCode.indexOf(GENERATED_CODE_BEGIN);
		if (startPos<0) {
			return;
		}
		startPos+=GENERATED_CODE_BEGIN.length()+1;
		
		int endPos = existingJavaSourceCode.indexOf(GENERATED_CODE_END);
		if (endPos<0) {
			return;
		}
		
		existingJavaSourceCode = existingJavaSourceCode.substring(0, startPos)+controllerJavaSourceCode+existingJavaSourceCode.substring(endPos);
		SynchronizableDocument ndoc = (SynchronizableDocument)doc;
		ndoc.set(existingJavaSourceCode);
		
		this.setActivePage(1);
	}

	
	private void prepareProjectClassLoader () throws Exception {
		projectClassLoader  = classLoadersForProjects.get(project.getName());
		if (projectClassLoader != null) return;
		
		IJavaProject javaProject = JavaCore.create(project);
		String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
		this.webAppPath = classPathEntries[0]+"/../../src/main/webapp/"; 
		
		List<URL> urlList = new ArrayList<URL>();
		for (int i = 0; i < classPathEntries.length; i++) {
			 String entry = classPathEntries[i];
			 IPath path = new Path(entry);
			 URL url = path.toFile().toURI().toURL();
			 urlList.add(url);
		}
		
		
		urlList.add(org.eclipse.jetty.servlet.listener.IntrospectorCleaner.class.getClassLoader().getResource(""));
		urlList.add(org.eclipse.jetty.webapp.WebAppContext.class.getClassLoader().getResource(""));
		urlList.add(Servlet.class.getClassLoader().getResource(""));
		urlList.add(ServletContextHandler.class.getClassLoader().getResource(""));
		urlList.add(ServletHolder.class.getClassLoader().getResource(""));
		
		
		URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
		projectClassLoader = new URLClassLoader(urls, Servlet.class.getClassLoader());
		
		classLoadersForProjects.put(project.getName(), projectClassLoader);
		
	}
	
	private boolean startJettyForCurrentProject () {
		String projectName = project.getName();
		server = jettyServersForProjects.get(projectName);
		if (server != null) {
			return true;
		}
		
		try {
			server = createJettyServerInstance ();
			server.prepare (jettyPortPull++, projectClassLoader);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if (!server.start ()) {
			return false;
		}
		
		jettyServersForProjects.put(projectName, server);
		return true;
	}
	

	public static void shutDown () {
		for (String key : jettyServersForProjects.keySet()) {
			IJettyServer jServer = jettyServersForProjects.get (key);
			jServer.stop ();
		}
	}
	
	private IJettyServer createJettyServerInstance() {
		//return new JettyServerProxy ();
		return new JettyUtil (webAppPath);
	}

	public void forceRefresh() {
		if (server == null)
			return;
		classLoadersForProjects.clear();
		try {
			prepareProjectClassLoader ();
			server.restart(projectClassLoader);
			Thread.currentThread().sleep(300);
			this.setActivePage(2);
			browser.refresh ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
