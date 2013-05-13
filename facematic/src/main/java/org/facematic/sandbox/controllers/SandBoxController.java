package org.facematic.sandbox.controllers;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.UI;

public class SandBoxController implements org.facematic.core.mvc.FmBaseController,  TabSheet.SelectedTabChangeListener {
	
	@FmViewComponent
	Component sourceTab;
	
	@FmViewComponent
	Component resultTab;
	
	@FmViewComponent
	TabSheet  tabSheet;
	
	@FmViewComponent
	TextArea  source;
	
	@FmViewComponent
	Panel result;

	@FmViewComponent
	Component controllerTab;
	
	@FmViewComponent
	TextArea controllerText;
	
	@Inject
	@FmUI
	FacematicUI ui;
	
//	@Inject
//	String someString;
	
	
	
	String    compiledSource = "";

	private Component content;
	
	
	public SandBoxController () {
		int k = 0;
		k++;
	}

	@Override
	public void init () {
		source.setValue("<Button caption=\"OK\"/>\n");
		tabSheet.addSelectedTabChangeListener(this);
	}
	
	
	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		Object tab = tabSheet.getSelectedTab();
		if (tab == resultTab) {
			recompile ();
			
		} else if (tab == controllerTab) {
			recompile ();
		}
		
	}

	private void prepareControllerSource() {
		String sourceXml = source.getValue();
		if (sourceXml== null) return;
		if (sourceXml.trim().equals("")) return;
		if (sourceXml.equals(compiledSource)) return;
		
		String result = "";
	}

	private void recompile() {
		String sourceXml = source.getValue();
		if (sourceXml== null) return;
		if (sourceXml.trim().equals("")) return;
		if (sourceXml.equals(compiledSource)) return;
		compiledSource = sourceXml;
		
		FaceProducer factory = new FaceProducer (ui);
		
		final StringBuilder controllerTextBuilder = new StringBuilder ("public class SampleController implements FmBaseController {\n");
		final StringBuilder importsTextBuilder = new StringBuilder ("import org.facematic.core.annotations.FmViewComponent;\nimport org.facematic.core.annotations.FmController;\nimport org.facematic.core.mvc.FmBaseController;\n");
		final Map<String, String> importedClasses = new HashMap ();
		
		factory.setStructureWatcher(new FaceProducer.NodeWatcher() {
			
			@Override
			public void putView(String name, Object view) {
				
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

		content = null;
		controllerText.setValue ("");
		try {
			content = factory.buildFromString (sourceXml);
			String controllert = ""+importsTextBuilder +"\n\n"+ 
					controllerTextBuilder+
					"\n\n     @Override\n     public void main () {\n          //TODO Here you can add event listeners for implementing business-logic.\n     }\n\n"+
					"}\n";
			controllerText.setValue (controllert);
		} catch (Exception e) {
			content = new Label (e.getMessage(), ContentMode.PREFORMATTED);
		}
		result.setContent(content);
				
	}

}
