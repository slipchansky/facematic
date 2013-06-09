package org.facematic.site.showcase.viewer;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.core.ui.custom.Html;
import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.annotations.ShowFiles;
import org.facematic.site.showcase.complex.ComplexExample;
import org.facematic.utils.StreamUtils;

import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;


@SuppressWarnings({ "unused", "serial" })
@FmView(name="org.facematic.site.showcase.viewer.ShowCaseViewer")
@ShowCase (part=ShowCase.EXAMPLES_OF_USE, caption = "ShowCase viewer", description = "How does the Show-Case viewer made", moreClasses={ShowCase.class, ShowFiles.class})
@ShowFiles(java = true, xml = true, sample=false)
public class ShowCaseViewer implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ShowCaseViewer.class);

    @Inject	
    @FmUI
    FacematicUI ui;

    @FmViewComponent(name="showCaseViewer")
    TabSheet showCaseViewer;

    @FmViewComponent(name="view")
    VerticalLayout view;

	private Class<?> caseClass;

    
    public ShowCaseViewer (Class<?> caseClass) {
       this.caseClass = caseClass;
    }
	
    @Override
	public void prepareContext(FaceProducer producer) {
	}
	
	// for preview purposes
	public ShowCaseViewer () {
	   this.caseClass = org.facematic.site.showcase.composite.CompositeExample.class;
	}
	
    @Override
    public void init () {
         
    	showAll (caseClass);
    	showCaseViewer.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				JavaScript.getCurrent().execute("prettyPrint ()");
			}
		});
    	JavaScript.getCurrent().execute("prettyPrint ();");
    }
    
    public void showAll (Class<?> mainClass) {
    
        ShowFiles showFilesAnnotation = mainClass.getAnnotation(ShowFiles.class);
        boolean showSample = true;
        
        if (showFilesAnnotation != null) {
            showSample = showFilesAnnotation.sample();
        }
        
        if (showSample) {
            addSamplePage(mainClass);
		}
		
		addReadMePage (mainClass);
    
    	showFilesForClass (mainClass);
    	ShowCase showCaseAnnotation = (ShowCase)mainClass.getAnnotation(ShowCase.class);
    	if (showCaseAnnotation != null) {
    		for (Class<?> c : showCaseAnnotation.moreClasses()) {
    			showFilesForClass (c);
    		}
    	}
    	
    }

	private void addReadMePage(Class<?> mainClass) {
	    String className = mainClass.getCanonicalName();
		String readMePath = className.substring(0, className.length()-mainClass.getSimpleName().length()).replace('.', '/')+"readme.txt";
		logger.trace ("readMePath="+readMePath);
		String readMe = StreamUtils.getResourceAsString(readMePath);
		if (readMe == null) return;
		showFile (readMePath, readMe);
	}

	private void addSamplePage(Class<?> mainClass) {
		FaceProducer producer = new FaceProducer (ui);
        try {
			Component result = producer.getViewFor(mainClass);
			VerticalLayout vl = new VerticalLayout (result);
			vl.setSizeFull();
			vl.setMargin(true);
			vl.setCaption(mainClass.getSimpleName()); 
			showCaseViewer.addTab(vl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void showFilesForClass (Class<?> theClass) {
    	
		String javaCodeName = theClass.getCanonicalName().replace('.', '/')+ ".java";
		String xmlCodeName  = null;
		String xmlSource = null;
		String javaSource = StreamUtils.getResourceAsString(javaCodeName);

		if (javaSource != null) {
			FmView fmViewAnnotation = (FmView) theClass
					.getAnnotation(FmView.class);
			if (fmViewAnnotation != null) {
				xmlCodeName = fmViewAnnotation.name();
				xmlCodeName = xmlCodeName.replace('.', '/') + ".xml";
				xmlSource = StreamUtils.getResourceAsString(xmlCodeName);
			}
		} else {
			xmlCodeName = theClass.getCanonicalName().replace('.', '/')+ ".xml";
			xmlSource = StreamUtils.getResourceAsString(xmlCodeName);
		}
		
		ShowFiles showFilesAnnotation = (ShowFiles)theClass.getAnnotation(ShowFiles.class);
		
		if (showFilesAnnotation != null) {
			if (!showFilesAnnotation.java()) {
				javaSource=null;
			}
			if (!showFilesAnnotation.xml()) {
				xmlSource = null;
			}
		}
		
		if (xmlSource != null) {
			
			showFile (xmlCodeName, xmlSource);
		}
		
		if (javaSource != null) {
			showFile (javaCodeName, javaSource);
		}

    }
    
    public void showFile (String fileName, String sourceCode) {
    	String a [] = fileName.split("/");
    	Html content = new Html ();
    	content.setSizeFull();
    	content.setSizeUndefined ();
		sourceCode = sourceCode.replace("<", "&lt;");
		sourceCode = sourceCode.replace(">", "&gt;");
		if (fileName.endsWith(".txt")) {
		   sourceCode = sourceCode.replace('[', '<');
		   sourceCode = sourceCode.replace(']', '>');
		}
    	content.setValue("<pre class=\"prettyprint\">"+sourceCode+"</pre >");
    	
    	VerticalLayout vl = new VerticalLayout (content);
    	vl.setSizeFull(); vl.setMargin(true);
    	vl.setCaption (a[a.length-1]);
    	showCaseViewer.addTab(vl);
    }
}

