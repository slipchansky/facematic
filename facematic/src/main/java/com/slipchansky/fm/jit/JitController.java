package com.slipchansky.fm.jit;

import java.lang.reflect.InvocationTargetException;

import org.dom4j.DocumentException;

import com.slipchansky.fm.factory.FaceFactory;
import com.slipchansky.fm.mvc.FaceController;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class JitController extends FaceController implements  TabSheet.SelectedTabChangeListener {
	
	Component sourceTab;
	Component resultTab;
	TabSheet  tabSheet;
	
	TextArea  source;
	Panel     result;
	String    compiledSource = "";
	
	
	public JitController () throws Exception {
		super ();
		build ();
		source.setValue("<Button caption=\"OK\"/>\n");
		tabSheet.addSelectedTabChangeListener(this);
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
