package org.felix.jal.eclipse.plugin.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.felix.jal.compiler.JalCompileResult;
import org.felix.jal.compiler.JalCompiler;
import org.felix.jal.compiler.JalErrors;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;
import org.felix.jal.eclipse.plugin.ui.preferences.JalPreferencesPage;
import org.felix.jal.eclipse.plugin.ui.views.CompilerMetricsView;
import org.felix.jal.lang.JalParser;

public class JalBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.felix.jal.eclipse.plugin.JalBuilder";
	private static final String JALCOMPILERPROBLEM_ID = "org.felix.jal.jalcompilerproblem";
	public static JalCompileResult lastCompileResult = null;
			
	class CompileResourceVisitor implements IResourceVisitor {
		private List<JalCompileResult> results = new ArrayList<JalCompileResult>();
		
		public boolean visit(IResource resource) {
			JalCompileResult result = JalBuilder.compile(resource);
			
			if (result != null) {
				results.add(result);
			}
			//return true to continue visiting children.
			return true;
		}		

		public List<JalCompileResult> getResults() {
			return results;
		}

		public void setResults(List<JalCompileResult> results) {
			this.results = results;
		}
	}
	
	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException {

		CompileResourceVisitor visitor = new CompileResourceVisitor();
		getProject().accept(visitor);
		
		List<JalCompileResult> results = visitor.getResults();
		for (Iterator<JalCompileResult> it = results.iterator();it.hasNext();) {
			JalCompileResult result = it.next();	
			
			// Display compilation result to console
			JalConsole.write(result.getResult());
		}
				
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
					IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
			
		return  new IProject[0];
	}
	
	public static JalCompileResult compile(IResource resource) {
		if ("jal".equals(resource.getFileExtension())) {
			String mainFile = resource.getLocation().toOSString();
			if (JalParser.isMainFile(mainFile)) {
				JalCompileResult result = compileResource(resource.getProject(), mainFile);
				updateMarkers(resource, result);
				return result;
			}
		}
		return null;
	}
	
	public static  JalCompileResult compileResource(IProject project, String mainFile) {
		
		StringBuffer libPath = new StringBuffer(); 
		List<IProject> prjList = new ArrayList<IProject>();
		IProject prj = project;
		prjList.add(prj);
		try {
			
			IProject[] refPrj = prj.getReferencedProjects();
			for (int i = 0;i<refPrj.length;i++)
				if (refPrj[i] != null && refPrj[i].getLocation() != null)
					prjList.add(refPrj[i]);
			
		} catch (CoreException e) {
			JalConsole.write(e);
		}
		
		for (Iterator<IProject> it = prjList.iterator();it.hasNext();) {
			IProject p = (IProject) it.next();
			List<File> prjSubDirs;
			try {
				if (p != null && p.getLocation()!=null){
					prjSubDirs = FileUtil.getFileListing(new File(p.getLocation().toOSString()));
					libPath.append(";"+p.getLocation().toOSString());
					for (Iterator<File> subIt = prjSubDirs.iterator();subIt.hasNext();) {
						File file = (File)subIt.next();
						
						// exclude some dirs like '.settings'
						if ((file.isDirectory())&&(!file.getName().startsWith("."))) {
							String path = file.getAbsolutePath();
							libPath.append(";"+path);
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		String libPathStr=libPath.toString();
		if (libPathStr.startsWith(";")) {
			libPathStr = libPathStr.substring(1);
		}

		lastCompileResult = null;
		
		if (PlatformUI.getWorkbench().getWorkbenchWindows().length > 0) {
			if (PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages().length > 0) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages()[0];
				if (page != null) {
					try {
					IViewPart view = page.findView(CompilerMetricsView.COMPILERMETRICS_VIEW_ID);
					
					if (view != null && view instanceof CompilerMetricsView) {
						CompilerMetricsView metrics = (CompilerMetricsView) view;
						metrics.refresh();													
					}
					}catch (Exception ex){}
				}
			}
		}		
		
		String options = JalPreferencesPage.getCompilerOptions();
		String COMPILER_PATH = JalPreferencesPage.getCompilerPath();
		JalCompileResult result = JalCompiler.compile(COMPILER_PATH, mainFile,
				libPathStr, options);
		
		lastCompileResult = result;
				
		if (PlatformUI.getWorkbench().getWorkbenchWindows().length > 0) {
			if (PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages().length > 0) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages()[0];
				if (page != null) {
					try {
					IViewPart view = page.findView(CompilerMetricsView.COMPILERMETRICS_VIEW_ID);
					
					if (view != null && view instanceof CompilerMetricsView) {
						CompilerMetricsView metrics = (CompilerMetricsView) view;
						metrics.refresh();													
					}
					}catch (Exception ex){}
				}
			}
		}
		
		return result;
	}
	
	/*
	 * Update markers to show errors and warnings
	 */
	public static void updateMarkers(IResource resource, JalCompileResult result) {
		/*
		 * Update Markers
		 */
		IMarker marker;
		try {
			// Delete Markers on all needed files
			IFile[] files = FileUtil.findAllReferencedFiles(resource.getProject());
			for (int i = 0; i < files.length; i++) {
				// Clear existing error markup
				IFile file = files[i];
				if (file != null) {
					int depth = IResource.DEPTH_INFINITE;
					try {
						file.deleteMarkers(JALCOMPILERPROBLEM_ID, true, depth);
					} catch (CoreException e) {
						JalConsole.write(e.getMessage());
					}
				}
			}

			// Add new Markers
			for (int i = 0; i < result.getErrors().length; i++) {
				JalErrors err = result.getErrors()[i];
				IFile file = FileUtil.toIFile(resource.getProject(), err
						.getFile());
				if (file != null) {
					marker = file.createMarker(JALCOMPILERPROBLEM_ID);
					int line = err.getLine(); // 1 based
					if (marker.exists()) {
						marker.setAttribute(IMarker.MESSAGE, err.getMessage());
						if (err.getMessage().startsWith(" warning: ")) {
							marker.setAttribute(IMarker.SEVERITY, new Integer(
									IMarker.SEVERITY_WARNING));
						} else {
							marker.setAttribute(IMarker.SEVERITY, new Integer(
									IMarker.SEVERITY_ERROR));
						}
						marker.setAttribute(IMarker.PRIORITY,
								IMarker.PRIORITY_HIGH);
						marker.setAttribute(IMarker.LINE_NUMBER, line);
						marker.setAttribute(IMarker.LOCATION, "Line " + line);
					}
				}
			}
		} catch (Exception e) {
			JalConsole.write(e);
		}
	}
	

}
