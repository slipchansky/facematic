package org.facematic.core.producer.builders;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.custom.Html;

import com.vaadin.ui.Label;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class HtmlBuilder extends ComponentBuilder {
	private final static Logger logger = LoggerFactory.getLogger(HtmlBuilder.class);

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
				text = builder.getTemplateEngine().translate(text);
			} catch (Exception e) {
				logger.error("Groovy could not translate\n"+text, e);
			}
		}
		
		((Label) oComponent).setValue(text);
	}

	/**
	 * @param configuration
	 * @return
	 */
	public String format(Element configuration) {
		return org.facematic.utils.XmlUtil.getInnerContent(configuration); 
	}

}
