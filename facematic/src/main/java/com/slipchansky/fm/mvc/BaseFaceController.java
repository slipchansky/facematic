package com.slipchansky.fm.mvc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.dom4j.DocumentException;

import com.slipchansky.fm.factory.FaceFactory;
import com.slipchansky.fm.mvc.annotations.FaceAccessor;
import com.slipchansky.fm.mvc.annotations.FaceContextAccessor;
import com.slipchansky.fm.mvc.annotations.FaceController;
import com.slipchansky.fm.mvc.annotations.FaceParent;
import com.vaadin.ui.Component;


@FaceController(viewName="composite", viewPath="com.test")
public class BaseFaceController {
	
	private static final String MODIFIERS_FIELD = "modifiers";
	
	// TODO implement annotations!!! См. FaceFactory
	private String viewName;
	private String viewPath;
	
	private FaceFactory factory = new FaceFactory ();
	private Component view;
	
	public BaseFaceController () {
		super ();
		viewName = getClass().getSimpleName();
		final String controllersSuffix = "Controller";
		final String controllersPachageSuffix = ".controllers";
		
		if (viewName.endsWith (controllersSuffix)) {
			viewName = viewName.substring(0, viewName.length ()-controllersSuffix.length());
		}
		viewName += "View";
		 
		viewPath = getClass().getPackage().getName();
		viewPath = viewPath.replace (controllersPachageSuffix+'.', ".views.");
		if (viewPath.endsWith(controllersSuffix)) {
			viewPath = viewPath.substring(0, viewPath.length ()-controllersPachageSuffix.length());
			viewPath += ".views";
		}
	}
	

	protected <T extends BaseFaceController> T build() throws Exception {
		implementPathAndViewNameAnnotations ();
		factory.put(FaceFactory.CONTEXT_CONTROLLER, this);
		
		view = factory.buildFromResource(viewPath+'.'+viewName);
		
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
				 modifiers |=  Modifier.PUBLIC;
				 modifiersField.setInt(field, modifiers);
				try { 
				 field.set(this, value);
				} catch (IllegalArgumentException e) {
					if (value instanceof Map) {
						Object newValue = factory.get(name+FaceFactory.CONTEXT_VIEW_SUFFIX);
						field.set(this, newValue );
					}
				}
				 
			} catch (NoSuchFieldException e) {
				 e.printStackTrace();
			}
			 
		}
		implementFieldsAnnotations(this);
		return (T)this;
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
	
	
	private void implementPathAndViewNameAnnotations () {
		Class clazz = getClass();
		com.slipchansky.fm.mvc.annotations.FaceController faceControllerAnnotation = (com.slipchansky.fm.mvc.annotations.FaceController) clazz.getAnnotation(FaceController.class);

		if (faceControllerAnnotation != null) {
			try {
				if (!"".equals(faceControllerAnnotation.viewName()))
					setFieldValue(this, clazz.getDeclaredField("viewName"), faceControllerAnnotation.viewName());
			} catch (Exception e) {
			}

			try {
				if (!"".equals(faceControllerAnnotation.viewPath()))
					setFieldValue(this, clazz.getDeclaredField("viewPath"),
							faceControllerAnnotation.viewPath());
			} catch (Exception e) {
			}
		}
		
	}
	
	/**
	 * имплементировать механизмы аннотирования!!!
	 * @param obj
	 */
	// TODO имплементировать механизмы аннотирования!!!
	private void implementFieldsAnnotations(Object obj) {
		Class clazz = obj.getClass();
		com.slipchansky.fm.mvc.annotations.FaceController faceControllerAnnotation = (com.slipchansky.fm.mvc.annotations.FaceController) clazz.getAnnotation(FaceController.class);

		for (Field field : clazz.getDeclaredFields()) {
			implementFieldAnnotations(obj, field);
		}

		int k = 0;
		k++;

	}

	private void implementFieldAnnotations(Object obj, Field field) {
		FaceParent   faceParent = field.getAnnotation(FaceParent.class);
		FaceAccessor faceAccessor = field.getAnnotation(FaceAccessor.class);
		FaceContextAccessor faceContextAccessor = field.getAnnotation(FaceContextAccessor.class);
		
		if ( faceParent != null ) {
			Object parent = factory.get ( FaceFactory.CONTEXT_PARENT_VIEW );
			if (parent != null) setFieldValue(obj, field, parent);
		}

		Object key = null;
		if (faceAccessor != null) {
			key = faceAccessor.path();
			if (key == null || "".equals(key))
				;
			key = field.getName();
		}

		if (faceContextAccessor != null) {
			key = field.getType();
		}

		Object value = factory.get(key);
		if (value != null) {
			setFieldValue(obj, field, value);
		}
	}

	private void setFieldValue(Object obj, Field field, Object value) {
		if (value == null)
			return;
		Field modifiersField;
		try {
			modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
			modifiersField.setAccessible(true);
			int modifiers = modifiersField.getInt(field);
			modifiers &= ~Modifier.FINAL;
			modifiers |= Modifier.PUBLIC;
			modifiersField.setInt(field, modifiers);
			try {
				field.set(this, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) throws Exception {
		BaseFaceController fc = new BaseFaceController  ();
		fc.build();
		Map context = fc.getFactory().getContext();
		String s = context.toString(); 
		int k=0;
		k++;
	}
}

