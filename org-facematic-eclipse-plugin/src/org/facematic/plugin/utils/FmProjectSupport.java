package org.facematic.plugin.utils;

import java.io.ByteArrayInputStream;
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
import org.eclipse.core.runtime.IPath;
import org.facematic.Activator;

public class FmProjectSupport {
	
	SimpleTemplateEngine velocity = new SimpleTemplateEngine ();
	private String projectName;
	private URI location;
	private boolean implement;
	private ProjectPomHelper pom;
	private IProject project;
	
	public FmProjectSupport (String projectName, URI location, Map<String, String> substs, boolean implement, ProjectPomHelper pom) {
		this.velocity.put(substs);
		this.projectName = projectName;
		this.location = location;
		this.implement = implement;
		this.pom = pom;
	}
	
	public FmProjectSupport (IProject project) {
		this.project = project;
		this.projectName = project.getName();
		this.implement = true;
		
		try {
			this.location = new URI (project.getLocation().toFile().getAbsolutePath().replace('\\', '/'));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


	public  IProject createProject() throws Exception {
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);
		try {
			IProject project = createBaseProject();
			return project;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

		
	}

	private  IProject createBaseProject() throws IOException, CoreException {

		this.project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if (!project.exists()) {
			URI projectLocation = location;
			
			

			IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());

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
				project.create(desc,  null);
				
				
			} catch (CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (!project.isOpen()) {
			project.open(null);
		}
		fillProject ();
		return project;
	}

	private  void fillProject() throws IOException, CoreException {
		List<Entry> entries = getProjectEntries ();
		for (Entry e : entries ) {
			if (e.isdir) {
				createProjectFolder(e);
			} else {
				if (implement)
					if (!e.name.startsWith("src/main/java/") && !e.name.startsWith("src/main/resources/") && !e.name.startsWith("src/main/webapp/VAADIN/"))
						continue;
				
				createFile (e);
			}
			
		}
		if (implement && pom != null) {
			pom.addFacematicFeatures ();
			updateFile ("pom.xml", new ByteArrayInputStream(pom.getSource().getBytes()));
		}
		
	}

	private void createProjectFolder(Entry e) throws CoreException {
		createProjectFolder (velocity.evaluateString(e.name));
		
	}

	private  void createFile(Entry e) throws CoreException {
		String fileName = velocity.evaluateString(e.name);
		InputStream is = prepareByTemplate(e);
		updateFile(fileName, is);
	}

	public void updateFile(String fileName,	InputStream is) throws CoreException {
		File file = (File) project.getFile(fileName);
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
	
	public void updateFile(String fileName,	String content) throws CoreException {
		updateFile(fileName, new ByteArrayInputStream (content.getBytes()));
	} 

	private  InputStream prepareByTemplate(Entry e) {
		String result = velocity.evaluateString(e.body);
		return new ByteArrayInputStream(result.getBytes());
	}

	private  IFolder createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}

		if (!folder.exists()) {
			folder.create(false, true, null);
		}

		return folder;
	}

	public  IFolder createProjectFolder(String path) throws CoreException {
		IFolder etcFolders = project.getFolder(path);
		return createFolder(etcFolders);
	}
	
	 class Entry {
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

	public  List<Entry> getProjectEntries () throws IOException {
		URL url = Activator.getResourceURL("templates/projects/fmfastbuild.zip");
		InputStream in = url.openStream();
		
		List<Entry> entries = getTemplateEntries(in);
		in.close();
		return entries;
	}

	private List<Entry> getTemplateEntries(InputStream is) throws IOException {
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
