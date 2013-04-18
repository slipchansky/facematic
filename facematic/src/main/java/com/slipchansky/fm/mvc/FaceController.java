package com.slipchansky.fm.mvc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.dom4j.DocumentException;

import com.slipchansky.fm.factory.FaceFactory;
import com.vaadin.ui.Component;


public class FaceController {
	private static final String MODIFIERS_FIELD = "modifiers";
	private FaceFactory factory = new FaceFactory ();
	private Component view;

	protected <T extends FaceController> T build() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException, DocumentException {
		String viewName = getClass().getCanonicalName(); 
		
		final String controolerSuffix = "Controller";
		final String controolerPachageSuffix = ".controllers.";
		if (viewName.endsWith (controolerSuffix)) {
			viewName = viewName.substring(0, viewName.length ()-controolerSuffix.length());
		}
		viewName = viewName.replace (".controllers.", ".views.");
		
		viewName += "View";
		
		view = factory.buildFromResource(viewName);
		
		Field[] fields = getClass ().getDeclaredFields();
		
		for (Field field : fields) {
			String name = field.getName();
			Object value = factory.get(name);
			if (value==null) {
				continue;
			}
			
			 Field modifiersField;
			try {
				 modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
				 modifiersField.setAccessible(true);
				 int modifiers = modifiersField.getInt(field);
				 modifiers &= ~Modifier.FINAL;
				 modifiers |= Modifier.PUBLIC;
				 modifiersField.setInt(field, modifiers);
				 field.set(this, value);
			} catch (NoSuchFieldException e) {
				 e.printStackTrace();
			}
			 
		}
		return (T)this;
	}
	
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ClassCastException, DocumentException, NoSuchFieldException, SecurityException {
		FaceController fc = new FaceController ();
	}

	public FaceFactory getFactory() {
		return factory;
	}

	public Component getView() {
		return view;
	}
	
	public void put (String name, Object value) {
		factory.put(name, value);
		
	}
}

