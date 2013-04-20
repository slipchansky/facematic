package com.slipchansky.fm.factory.builders;

import java.lang.reflect.InvocationTargetException;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.slipchansky.fm.factory.FaceFactory;
import com.slipchansky.fm.mvc.BaseFaceController;
import com.slipchansky.fm.mvc.annotations.FaceController;
import com.slipchansky.fm.ui.Composite;

public class CompositeBuilder extends PanelBuilder {

	enum Locations {
		RESOURCE, FILE, URL
	}

	@Override
	public Class getBuildingClass() {
		return Composite.class;
	}

	@Override
	protected Object prepareInnerComponent(FaceFactory builder,
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
			FaceFactory nestedBuilder = new FaceFactory();
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
			
			
			
			Object currentContext = builder.getContext().get(name);
			nestedBuilder.put (FaceFactory.CONTEXT_PARENT_CONTEXT,       builder.getContext());
			nestedBuilder.put (FaceFactory.CONTEXT_PARENT_VIEW,          builder.get (FaceFactory.CONTEXT_VIEW));
			nestedBuilder.put (FaceFactory.CONTEXT_PARENT_CONTROLLER,    builder.get (FaceFactory.CONTEXT_CONTROLLER));
			nestedBuilder.put (FaceFactory.CONTEXT_VIEW_SUFFIX,          currentContext);
			
			nestedBuilder.getContext().put("applicationUI", builder.getContext().get("applicationUI"));
			if (name != null) builder.getContext().put(name+FaceFactory.CONTEXT_VIEW_SUFFIX,  currentContext);
			if (name != null) builder.getContext().put(name, nestedBuilder.getContext());
			break;
		case FILE:
		case URL:
			throw new RuntimeException("Not implemented yet");
		}

		return result;

	}

}
