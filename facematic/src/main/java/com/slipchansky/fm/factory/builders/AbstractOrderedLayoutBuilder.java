package com.slipchansky.fm.factory.builders;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractOrderedLayout;

public class AbstractOrderedLayoutBuilder extends ComponentContainerBuilder {

	
	
	final static Map<String, com.vaadin.ui.Alignment> alignments = new HashMap<String, com.vaadin.ui.Alignment> () {{
		put ("BOTTOM_CENTER", Alignment.BOTTOM_CENTER);
		put ("BOTTOM_LEFT", Alignment.BOTTOM_LEFT);
		put ("BOTTOM_RIGHT", Alignment.BOTTOM_RIGHT);
		put ("MIDDLE_CENTER", Alignment.MIDDLE_CENTER);
		put ("MIDDLE_LEFT", Alignment.MIDDLE_LEFT);
		put ("MIDDLE_RIGHT", Alignment.MIDDLE_RIGHT);
		put ("TOP_CENTER", Alignment.TOP_CENTER);
		put ("TOP_LEFT", Alignment.TOP_LEFT);
		put ("TOP_RIGHT", Alignment.TOP_RIGHT);
	}};
	
	

	@Override
	public Class getBuildingClass() {
		return AbstractOrderedLayout.class;
	}

	
	@Override
	protected void addComponent(AbstractComponentContainer oComponent, AbstractComponent component, Element node) {
		super.addComponent(oComponent, component, node);
		AbstractOrderedLayout layout = (AbstractOrderedLayout)oComponent;
		
		applyAlignment(layout, component, node);
		applyExpandRatio (layout, component, node);
		
	}

	private void applyAlignment(AbstractOrderedLayout layout,
			AbstractComponent component, Element node) {
		Attribute alignmentNode = node.attribute ("alignment");
		if (alignmentNode==null) {
		    return;
		}
		
		try {
		    Alignment alignment = alignments.get(alignmentNode.getValue().toUpperCase());
		    if (alignment != null) {
		      layout.setComponentAlignment(component, alignment);
		    }
		} catch (Exception e) {
			//skip;
		}
	}
	
	private void applyExpandRatio(AbstractOrderedLayout layout,
			AbstractComponent component, Element node) {
		Attribute expandRatioAttribute = node.attribute ("expandRatio");
		if (expandRatioAttribute==null) {
		    return;
		}
		
		try {
			Float ratio = Float.valueOf(expandRatioAttribute.getValue());
			layout.setExpandRatio(component, ratio.floatValue());
		} catch (Exception e) {
			//skip;
		}
	}
	

}
