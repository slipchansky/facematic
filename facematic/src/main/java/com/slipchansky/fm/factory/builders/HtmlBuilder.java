package com.slipchansky.fm.factory.builders;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.groovy.control.CompilationFailedException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gwt.user.client.ui.HTML;
import com.slipchansky.fm.factory.ComponentFactory;
import com.slipchansky.fm.ui.Html;
import com.vaadin.ui.Label;

public class HtmlBuilder extends ComponentBuilder {

	@Override
	public Class getBuildingClass() {
		return Html.class;
	}

	@Override
	public void build(ComponentFactory builder, Object oComponent,
			Element configuration) {
		super.build(builder, oComponent, configuration);

		String text = format(configuration);
		if (text.indexOf('$') > 0) {
			try {
				text = builder.getEngine().translate(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		((Label) oComponent).setValue(text);
	}

	public String format(Element configuration) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter swriter = new StringWriter();
		XMLWriter writer = new XMLWriter(swriter, format);
		try {
			writer.write(configuration);
			writer.flush();
			return swriter.getBuffer().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
