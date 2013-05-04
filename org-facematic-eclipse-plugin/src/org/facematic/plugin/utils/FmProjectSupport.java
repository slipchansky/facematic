package org.facematic.plugin.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.facematic.Activator;

public class FmProjectSupport {
	
	


	public static IProject createProject(String projectName, URI location, Map<String, String> substs) throws Exception {
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);
		
		VelocityEvaluator velocity = new VelocityEvaluator  (substs);

		
		try {
			IProject project = createBaseProject(projectName, location, velocity);
			return project;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

		
	}

	private static IProject createBaseProject(String projectName, URI location, VelocityEvaluator velocity) throws IOException, CoreException {

		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if (!newProject.exists()) {
			URI projectLocation = location;
			
			

			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());

			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
				projectLocation = null;
			} else {
				
				if (location != null) {
					String uri = location.toString();
					if (!uri.endsWith("/")) uri += "/";
					uri+= projectName;
				    new java.io.File (uri).mkdirs ();
				    try {
				    	projectLocation = new URI (uri);
					} catch (URISyntaxException e) {
					}
				}
				
			}
			

			desc.setLocationURI(projectLocation);
			try {
				newProject.create(desc,  null);
				
				if (!newProject.isOpen()) {
					newProject.open(null);
				}
				fillProject (newProject, velocity);
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return newProject;
	}

	private static void fillProject(IProject newProject, VelocityEvaluator velocity) throws IOException, CoreException {
		List<Entry> entries = getFakeEntries ();
		for (Entry e : entries ) {
			if (e.isdir) {
				createProjectFolder(newProject, e, velocity);
			} else {
				createFile (newProject, e, velocity);
			}
			
		}
		
	}

	private static void createProjectFolder(IProject newProject, Entry e, VelocityEvaluator velocity) throws CoreException {
		createProjectFolder (newProject, velocity.evaluateString(e.name));
		
	}

	private static void createFile(IProject project, Entry e, VelocityEvaluator velocity) throws CoreException {
		String fileName = velocity.evaluateString(e.name);
		File file = (File) project.getFile(fileName);
		InputStream is = prepareByTemplate(e, velocity);
		if (!file.exists()) {
			file.create(is, true, null);
		} else {
			file.setContents(is, false, false, null);
		}
		try {
			is.close();
		} catch (IOException e1) {
		}
		
		
		
	}

	private static InputStream prepareByTemplate(Entry e, VelocityEvaluator velocity) {
		String result = velocity.evaluateString(e.body);
		return new ByteArrayInputStream(result.getBytes());
	}

	private static IFolder createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}

		if (!folder.exists()) {
			folder.create(false, true, null);
		}

		return folder;
	}

	private static IFolder createProjectFolder(IProject newProject, String path)
			throws CoreException {
		IFolder etcFolders = newProject.getFolder(path);
		return createFolder(etcFolders);
	}
	
	static class Entry {
		public String name;
		public String body;
		public boolean isdir;
		public Entry(String name, String body, boolean isdir) {
			super();
			this.name = name;
			this.body = body;
			this.isdir = isdir;
		}
	}

	public static List<Entry> getFakeEntries () throws IOException {
		URL url = Activator.getResourceURL("templates/projects/fmfastbuild.zip");
		InputStream in = url.openStream();
		
		//FileInputStream fis = new FileInputStream("Z:/!chuma/org-facematic-eclipse-plugin/templates/projects/fmfastbuild.zip");
		List<Entry> entries = getTemplateEntries(in);
		//fis.close();
		in.close();
		return entries;
	}

	private static List<Entry> getTemplateEntries(InputStream is) throws IOException {
		ZipInputStream zis = new ZipInputStream(is);

		ZipEntry ze;
		String projectBody;
		List<Entry> entries = new ArrayList<Entry> ();
		
		while ((ze = zis.getNextEntry()) != null) {
			String body = StreamUtils.getString(zis);
			entries.add(new Entry (ze.getName(), body, ze.isDirectory()));
			zis.closeEntry();
		}
		zis.close ();
		return entries;
	}

}
