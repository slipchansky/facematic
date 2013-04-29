package org.facematic.facematic.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.ide.IDE;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.DummyFacematicUi;
import org.facematic.plugin.utils.ProjectResourceAccessor;
import org.facematic.plugin.utils.VelocityEvaluator;
import org.facematic.utils.ExternalResourceClassLoader;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * <ul>
 * <li>page 0 contains xml editor.
 * <li>page 1 contains java editor
 * </ul>
 */
public class FmMvcEditor extends MultiPageEditorPart implements
		IResourceChangeListener {
	final static String MAIN_JAVA = "main.java.";
	final static String MAIN_RESOURCES = "main.resources.";
	final static String VIEWS_PACKAGE_SUFFIX = ".views";
	final static String CONTROLLERS_PACKAGE_SUFFIX = ".controllers";
	final static String CONTROLLER_SUFFIX = "Controller";
	final static String VIEW_SUFFIX = "View";
	final static String GENERATED_CODE_BEGIN = "//GENERATED_CODE_BEGIN. Do not replace this comment!";
	final static String GENERATED_CODE_END = "//GENERATED_CODE_END. Do not replace this comment!";

	private VelocityEvaluator velocityEvaluator;

	private TextEditor xmlEditor;
	private MyJavaEditor javaEditor;

	private Font font;
	private StyledText text;

	File xmlFile;
	File javaFile;
	private boolean useJavaResource = false;
	private String viewPackageName;
	private String controllerPackageName;
	private String viewName;
	private String controllerName;
	private IContainer project;
	private FileEditorInput xmlEditorInput;
	private FileEditorInput javaEditorInput;

	/**
	 * Creates a multi-page editor example.
	 */
	public FmMvcEditor() {
		super();

	}

	private void createXmlEditor() {
		try {
			xmlEditor = new MyXmlEditor(this);
			xmlEditorInput = new FileEditorInput(xmlFile);
			int index = addPage(xmlEditor, xmlEditorInput);
			setPageText(index, xmlEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	private void createJavaEditor() {
		try {
			javaEditor = new MyJavaEditor();
			javaEditorInput = new FileEditorInput(javaFile);
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
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void init(IEditorSite site, IEditorInput edInput)
			throws PartInitException {

		try {
			velocityEvaluator = new VelocityEvaluator();
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

		if (!(edInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");

		FileEditorInput editorInput = (FileEditorInput) edInput;
		File file = (File) editorInput.getFile(); // /sss/src/main/resources/com/stas/views/composite.xml
		Workspace workspace = (Workspace) file.getWorkspace();
		workspace.addResourceChangeListener(this);

		IContainer container = file.getParent(); // /sss/src/main/resources/com/stas/views
		project = file.getProject(); // /sss
		IPath fullPath = project.getFullPath();

		ProjectResourceAccessor acc = new ProjectResourceAccessor(project);
		InputStream is = acc.getResourceStream("com/test/Test.java");

		String fileName = file.getProjectRelativePath().toString();
		String title = file.getName();

		String a[] = container.toString().split("/src/");
		String packageBaseName = a[1].replace("/", ".");
		a = fileName.split("/");
		String baseVcName = a[a.length - 1].replace(".xml", "")
				.replace(".java", "").replace(".fml", "");

		packageBaseName = updatePrefix(packageBaseName, MAIN_JAVA);
		packageBaseName = updatePrefix(packageBaseName, MAIN_RESOURCES);

		viewPackageName = updateSuffix(packageBaseName, VIEWS_PACKAGE_SUFFIX,
				CONTROLLERS_PACKAGE_SUFFIX);
		controllerPackageName = updateSuffix(packageBaseName,
				CONTROLLERS_PACKAGE_SUFFIX, VIEWS_PACKAGE_SUFFIX);

		viewName = updateSuffix(baseVcName, VIEW_SUFFIX, CONTROLLER_SUFFIX);
		controllerName = updateSuffix(baseVcName, CONTROLLER_SUFFIX,
				VIEW_SUFFIX);

		velocityEvaluator.put("viewPackage", viewPackageName);
		velocityEvaluator.put("controllerPackage", controllerPackageName);
		velocityEvaluator.put("controller", controllerName);
		velocityEvaluator.put("view", viewName);
		velocityEvaluator.put("GENERATED_CODE_BEGIN", GENERATED_CODE_BEGIN);
		velocityEvaluator.put("GENERATED_CODE_END", GENERATED_CODE_END);

		javaFile = updateController();
		xmlFile = updateView();
		this.setTitle(baseVcName);

		super.init(site, edInput);

	}

	private File updateController() {
		return updateSourceFile(controllerPackageName, controllerName,
				MAIN_JAVA, ".java", "controller.vm");
	}

	private File updateView() {
		return updateSourceFile(viewPackageName, viewName, MAIN_RESOURCES,
				".xml", "view.vm");
	}

	private File updateSourceFile(String packageName, String artefactName,
			String mvnPrefix, String ext, String templateName) {
		String packagePath = packageName;
		if (useJavaResource) {
			packagePath = "src." + mvnPrefix + packageName;
		} else
			packagePath = "src." + packageName;

		packagePath = packagePath.replace('.', '/');
		String sourceFileName = packagePath + '/' + artefactName + ext;

		Path javaPath = new Path(sourceFileName);
		File file = (File) project.getFile(javaPath);
		if (!file.exists()) {
			try {
				file.create(prepareSource(templateName), true, null);
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
					original.length() - invalidSuffix.length())
					+ validSuffix;
		}
		return original;
	}

	private String updatePrefix(String string, final String prefix) {

		if (string.startsWith(prefix)) {
			string = string.substring(prefix.length());
			useJavaResource = true;
		}
		return string;
	}

	private InputStream prepareSource(String templateName) {
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
		// producer.buildFromResource(resourceName)
		// org.facematic.utils.StreamUtils.getResourceAsString(arg0)

		try {
			//InputStream is = xmlEditorInput.getFile().getContents();
//			String markup = org.facematic.utils.StreamUtils.getString(is);
//			is.close();
			
			final  IDocument doc = (IDocument) xmlEditor.getAdapter(IDocument.class);
			String markup = doc.get();
			
			compileController (markup);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void compileController(String markup) {
		ProjectResourceAccessor projectResourceAccessor = new ProjectResourceAccessor(project);
		org.facematic.utils.StreamUtils.setResourceAccessor(projectResourceAccessor);
		FaceProducer producer = new FaceProducer(new DummyFacematicUi());
		
		producer.setCustomClassLoader(new ExternalResourceClassLoader (projectResourceAccessor));
		
		final StringBuilder importsTextBuilder = new StringBuilder ("import org.facematic.core.annotations.FmViewComponent;\nimport org.facematic.core.annotations.FmController;\nimport org.facematic.core.mvc.FmBaseController;\n");
		final StringBuilder controllerTextBuilder = new StringBuilder ("public class "+controllerName+" implements FmBaseController {\n");
		final Map<String, String> importedClasses = new HashMap ();
		
		producer.setStructureListener(new FaceProducer.NodeWatcher() {
			
			@Override
			public void putView(Object name, Component view) {
				
				controllerTextBuilder.append("     @FmViewComponent(name=\""+name+"\")\n");
				controllerTextBuilder.append("     private "+view.getClass().getSimpleName()+" "+name+";\n\n");
				if (importedClasses.get (view.getClass().getCanonicalName())==null) {
				   importsTextBuilder.append("import "+ view.getClass().getCanonicalName()+";\n");
				   importedClasses.put (view.getClass().getCanonicalName(), "");
				}
			}
			
			@Override
			public void putController(String name, Object controller) {
				controllerTextBuilder.append("     @FmController(name=\""+name+"\")\n");
				controllerTextBuilder.append("     private "+controller.getClass().getSimpleName()+" "+name+";\n\n");
				if (importedClasses.get (controller.getClass().getCanonicalName())==null) {
				   importsTextBuilder.append("import "+ controller.getClass().getCanonicalName()+";\n");
				   importedClasses.put (controller.getClass().getCanonicalName(), "");
				}
			}
		});

		
		try {
			producer.buildFromString (markup);
		} catch (Exception e) {
			return;
		}
		
		
		String controllert = importsTextBuilder+"\n\n"+controllerTextBuilder;
		
		
		IDocument doc = javaEditor.getViewer().getDocument(); //SynchronizableDocument
		
		String source = javaEditor.getViewer().getDocument().get();
		
		
		int startPos = source.indexOf(GENERATED_CODE_BEGIN);
		if (startPos<0) {
			return;
		}
		startPos+=GENERATED_CODE_BEGIN.length()+1;
		int endPos = source.indexOf(GENERATED_CODE_END);
		if (endPos<0) {
			return;
		}
		
		source = source.substring(0, startPos)+controllert+source.substring(endPos);
		SynchronizableDocument ndoc = (SynchronizableDocument)doc;
		ndoc.set(source);
		this.setActivePage(1);
		
	}

}
