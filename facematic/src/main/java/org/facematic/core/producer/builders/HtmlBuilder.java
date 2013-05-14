package org.facematic.core.producer.builders;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.groovy.control.CompilationFailedException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gwt.user.client.ui.HTML;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Html;

import com.vaadin.ui.Label;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class HtmlBuilder extends ComponentBuilder {

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return Html.class;
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.ComponentBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object oComponent,
			Element configuration) {
		super.build(builder, oComponent, configuration);

		String text = format(configuration);
		if (text.indexOf('$') > 0) {
			try {
				text = builder.getGroovyEngine().translate(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		((Label) oComponent).setValue(text);
	}

	/**
	 * @param configuration
	 * @return
	 */
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
