package com.slipchansky.fm.factory.builders;

import org.dom4j.Element;

import com.slipchansky.fm.factory.FaceFactory;
import com.vaadin.ui.AbstractComponent;
import org.dom4j.tree.DefaultAttribute;
import java.lang.reflect.Method;
import java.util.List;

public class BeanBuilder {

	public Class getBuildingClass () {
		return Object.class;
	}
	
	public void build (FaceFactory builder, Object oInstance, Element configuration) {
		applyAttrinutes (oInstance, configuration);
	}
	
	private void applyAttrinutes (Object instance, Element node) {
		List<DefaultAttribute> attributes = node.attributes();
		if (attributes == null) {
			return ;
		}
			
		for (DefaultAttribute attr : attributes) {
			String name = attr.getName();
			String value = attr.getValue();
			name = "set"+name.substring(0, 1).toUpperCase()+name.substring(1);
			try {
			    Method setter = instance.getClass().getMethod(name, String.class);
			    setter.invoke(instance, value);
			} catch (Exception e) {
				try {
				Method setter = instance.getClass().getMethod(name, Boolean.TYPE);
				setter.invoke(instance, value.toLowerCase().equals("true"));
				} catch (Exception e1) {
					
					try {
						Method setter = instance.getClass().getMethod(name, Integer.TYPE);
						setter.invoke(instance, new Integer (value));
						} catch (Exception e2) {
							// skip
						}
				}
			}
		}
	}
	

}
