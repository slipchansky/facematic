package com.slipchansky.fm.jit;

import java.lang.reflect.InvocationTargetException;

import org.dom4j.DocumentException;

import com.slipchansky.fm.factory.FaceFactory;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class JitController implements  TabSheet.SelectedTabChangeListener {
	
	Component sourceTab;
	Component resultTab;
	TabSheet  tabSheet;
	
	TextArea  source;
	Panel     result;
	String     compiledSource = "";
	
	
	public JitController (FaceFactory env) {
		sourceTab = env.get("sourceTab");
		resultTab = env.get("resultTab");
		tabSheet = env.get("tabSheet");
		source = env.get("source");
		result = env.get("result");
		
		source.setValue("<Button caption=\"OK\"/>\n");
		
		tabSheet.addSelectedTabChangeListener(this);
	}
	
	public static Component getView () throws Exception {
		FaceFactory factory = new FaceFactory ();
		Component result = factory.buildFromResource(JitController.class.getPackage().getName()+".JIT");
		JitController viewController = new JitController (factory);
		return result;
	}

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		
		Object tab = tabSheet.getSelectedTab();
		
		if (tab == resultTab) {
			recompile ();
			
		}
		
	}

	private void recompile() {
		String sourceXml = source.getValue();
		if (sourceXml== null) return;
		if (sourceXml.trim().equals("")) return;
		if (sourceXml.equals(compiledSource)) return;
		compiledSource = sourceXml;
		
		FaceFactory factory = new FaceFactory ();

		Component content = null;
		try {
			content = factory.buildFromString (sourceXml);
		} catch (Exception e) {
			content = new Label (e.getMessage(), ContentMode.PREFORMATTED);
		}
		
		result.setContent(content);
		
	}
	
	

}
