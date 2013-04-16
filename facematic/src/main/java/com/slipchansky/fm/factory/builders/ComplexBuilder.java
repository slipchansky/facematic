package com.slipchansky.fm.factory.builders;

import java.lang.reflect.InvocationTargetException;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.slipchansky.fm.factory.ComponentFactory;
import com.slipchansky.fm.ui.Complex;

public class ComplexBuilder extends PanelBuilder {

	enum Locations {
		RESOURCE, FILE, URL
	}

	@Override
	public Class getBuildingClass() {
		return Complex.class;
	}

	@Override
	protected Object prepareInnerComponent(ComponentFactory builder,
			Element firstNested) {

		String sLocation = firstNested.attributeValue("location");
		String path = firstNested.attributeValue("path");
		String name = firstNested.attributeValue("name");

		if (sLocation == null)
			sLocation = "RESOURCE";

		Locations location = null;
		Object result = null;
		try {
			location = Locations.valueOf(sLocation);
		} catch (Exception e) {
			// TODO logging
			e.printStackTrace();
		}

		switch (location) {
		case RESOURCE:
			ComponentFactory nestedBuilder = new ComponentFactory();
			try {
				result = nestedBuilder.buildFromResource(path);
				if (result == null) {
					String markupPath = builder.get ("markupPath");
					String a[] = markupPath.split("/");
					markupPath = markupPath.replace(a[a.length-1], "")+(path.endsWith(".xml")?path:(path+".xml"));
					result = nestedBuilder.buildFromResource(markupPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			//builder.getContext().putAll(nestedBuilder.getContext());
			
			Object currentContext = builder.getContext().get(name);
			nestedBuilder.getContext().put ("view", currentContext);
			nestedBuilder.getContext().put("applicationUI", builder.getContext().get("applicationUI"));
			builder.getContext().put(name+"_view",  currentContext);
			builder.getContext().put(name, nestedBuilder.getContext());
			break;
		case FILE:
		case URL:
			throw new RuntimeException("Not implemented yet");
		}

		return result;

	}

}
