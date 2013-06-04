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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;


@FmView(name="org.facematic.site.showcase.viewer.ShowCaseViewer")
public class ShowCaseViewer implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ShowCaseViewer.class);

    @Inject	
    @FmUI
    FacematicUI ui;

    @FmViewComponent(name="showCaseViewer")
    TabSheet showCaseViewer;

    @FmViewComponent(name="view")
    VerticalLayout view;

	private Class mainClass;
    
    public ShowCaseViewer () {
    }
	
    @Override
	public void prepareContext(FaceProducer producer) {
	}
	
    @Override
    public void init () {
    	showAll (org.facematic.site.showcase.composite.CompositeExample.class);
    	showCaseViewer.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				JavaScript.getCurrent().execute("prettyPrint ()");
			}
		});
    	JavaScript.getCurrent().execute("prettyPrint ();");
    }
    
    public void showAll (Class mainClass) {
        FaceProducer producer = new FaceProducer (ui);
        try {
			Component result = producer.getViewFor(mainClass);
			result.setCaption(mainClass.getSimpleName());
			showCaseViewer.addTab(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    
    	showFilesForClass (mainClass);
    	ShowCase showCaseAnnotation = (ShowCase)mainClass.getAnnotation(ShowCase.class);
    	if (showCaseAnnotation != null) {
    		for (Class c : showCaseAnnotation.moreClasses()) {
    			showFilesForClass (c);
    		}
    	}
    	
    }
    
    public void showFilesForClass (Class theClass) {
    	
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
    	Panel tab = new Panel ();
    	tab.setSizeFull();
    	Label content = new Label ();
    	content.setContentMode(ContentMode.PREFORMATTED);
    	content.setSizeUndefined ();
    	content.setValue(sourceCode);
    	tab.setContent(content);
    	tab.setCaption (a[a.length-1]);
    	showCaseViewer.addTab(tab);
    }
	
    
}

