package com.slipchansky.fm.factory.builders;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.dom4j.Element;

import com.slipchansky.fm.factory.ComponentFactory;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponentContainer;

public class ComponentContainerBuilder extends ComponentBuilder {

	public Class getBuildingClass () {
		return AbstractComponentContainer.class;
	}
		

	@Override
	public void build(ComponentFactory builder,  Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		
		List<Element> elements = configuration.elements ();
		Element componentsNode = configuration.element ("components");
		if (componentsNode != null) {
			elements  = componentsNode.elements ();
		}
		
		
		
		for (Element e : elements) {
		     buildNestedComponent (builder, oComponent, e);
		}
		
		
	}


	private void buildNestedComponent(ComponentFactory builder, Object oComponent, Element node) {
		try {
			Object inner = builder.build(node);
			if (inner != null) {
				if (inner instanceof AbstractComponent) {
					
					AbstractComponentContainer container = (AbstractComponentContainer)oComponent;
					AbstractComponent component = (AbstractComponent) inner;
					addComponent(container, component, node);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	protected void addComponent(AbstractComponentContainer container,
			AbstractComponent component, Element node) {
		container.addComponent(component);
	}

	
}
