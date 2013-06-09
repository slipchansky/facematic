package org.facematic.site.sandbox;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.core.ui.custom.Html;
import org.facematic.site.showcase.empty.EmptyCase;
import org.facematic.utils.StreamUtils;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;

@FmView(name = "org.facematic.site.sandbox.SandBox")
public class SandBox implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SandBox.class);

	@Inject
	@FmUI
	FacematicUI ui;

	@FmViewComponent(name = "tabSheet")
	TabSheet tabSheet;

	@FmViewComponent(name = "sourceTab")
	VerticalLayout sourceTab;

	@FmViewComponent(name = "source")
	TextArea source;

	@FmViewComponent(name = "resultTab")
	VerticalLayout resultTab;

	@FmViewComponent(name = "instructions")
	VerticalLayout instructions;

	@FmViewComponent(name = "view")
	HorizontalLayout view;
	
	@FmViewComponent(name = "snippets")
	Table snippets;

	private String compiledSource = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.facematic.core.mvc.FmBaseController#prepareContext(org.facematic.
	 * core.producer.FaceProducer)
	 */
	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
					updateResult();
			}
		});
	}
	
	
	private void updateResult() {
		String sourceXml = source.getValue();
		if (sourceXml== null) return;
		if (sourceXml.trim().equals("")) return;
		if (sourceXml.equals(compiledSource)) return;
		compiledSource = sourceXml;
		
		FaceProducer factory = new FaceProducer (ui);
		Component content = null;
		try {
			content = factory.buildFromString (sourceXml);
		} catch (Exception e) {
			content = new Label (e.getMessage(), ContentMode.PREFORMATTED);
		}
		resultTab.removeAllComponents();
		resultTab.addComponent(content);
	}
	

	@FmReaction("Button[caption='Empty'].onClick")
	public void tryEmpty(ClickEvent event) {
		prepareSample(org.facematic.site.showcase.empty.EmptyCase.class);
	}

	@FmReaction("Button[caption='Elements'].onClick")
	public void tryElements(ClickEvent event) {
		prepareSample(org.facematic.site.showcase.simple.SimpleComponentsExample.class);
	}

	@FmReaction("Button[caption='Containers'].onClick")
	public void tryContainers(ClickEvent event) {
		prepareSample(org.facematic.site.showcase.containers.ContainersExample.class);
	}

	@FmReaction("Button[caption='Table'].onClick")
	public void tryTable(ClickEvent event) {
		prepareSample(org.facematic.site.showcase.table.TableExample.class);
	}

	@FmReaction("Button[caption='Tree'].onClick")
	public void tryTree(ClickEvent event) {
		prepareSample(org.facematic.site.showcase.tree.TreeExample.class);
	}

	@FmReaction("Button[caption='Complex'].onClick")
	public void tryComplex(ClickEvent event) {
		prepareSample(org.facematic.site.showcase.complex.ComplexExample.class);
	}

	private void prepareSample(Class<?> clazz) {
		FmView fmViewAnnotation = clazz.getAnnotation(FmView.class);
		String viewName = fmViewAnnotation.name();
		String viewPath = viewName.replace('.', '/') + ".xml";
		String sourceCode = StreamUtils.getResourceAsString(viewPath);
		source.setValue(sourceCode);

		String className = clazz.getCanonicalName();
		String readMePath = className.substring(0,
				className.length() - clazz.getSimpleName().length()).replace(
				'.', '/')
				+ "readme.txt";
		String readMe = StreamUtils.getResourceAsString(readMePath);
		if (readMe == null)
			return;

		Html content = new Html();
		content.setSizeFull();
		content.setSizeUndefined();
		readMe = readMe.replace("<", "&lt;");
		readMe = readMe.replace(">", "&gt;");
		readMe = readMe.replace('[', '<');
		readMe = readMe.replace(']', '>');
		content.setValue("<pre class=\"prettyprint\">" + readMe + "</pre >");
		instructions.removeAllComponents();
		instructions.addComponent(content);
		tabSheet.setSelectedTab(0);

	}

}
