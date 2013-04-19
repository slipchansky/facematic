package com.slipchansky.vabuilder.tests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.slipchansky.fm.mvc.annotations.FaceAccessor;
import com.slipchansky.fm.mvc.annotations.FaceContextAccessor;
import com.slipchansky.fm.mvc.annotations.FaceController;

@FaceController(viewName = "com.stas.mama")
public class TestAnnotations {
	private static final String MODIFIERS_FIELD = "modifiers";

	private String viewName;
	private String viewPath;

	@FaceAccessor
	private Integer myField;

	@FaceContextAccessor(key = "key")
	private String someString;

	Map<Object, Object> context = new HashMap() {
		{   put("myField", 10);
			put(String.class, "The String");

		}
	};

	public static void main(String[] args) {

		TestAnnotations t = new TestAnnotations();
		t.implementAnnotations(t);
		int k = 0;
		k++;

	}

	private void implementAnnotations(Object obj) {
		Class clazz = obj.getClass();
		FaceController faceControllerAnnotation = (FaceController) clazz.getAnnotation(FaceController.class);

		if (faceControllerAnnotation != null) {
			try {
				if (!"".equals(faceControllerAnnotation.viewName()))
					setFieldValue(obj, clazz.getDeclaredField("viewName"),
							faceControllerAnnotation.viewName());
			} catch (Exception e) {
			}

			try {
				if (!"".equals(faceControllerAnnotation.viewPath()))
					setFieldValue(obj, clazz.getDeclaredField("viewPath"),
							faceControllerAnnotation.viewPath());
			} catch (Exception e) {
			}
		}

		for (Field field : clazz.getDeclaredFields()) {
			implementFieldAnnotations(obj, field);
		}

		int k = 0;
		k++;

	}

	private void implementFieldAnnotations(Object obj, Field field) {
		FaceAccessor faceAccessor = field.getAnnotation(FaceAccessor.class);
		FaceContextAccessor faceContextAccessor = field
				.getAnnotation(FaceContextAccessor.class);

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

		Object value = get(key);
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

	private Object get(Object path) {
		return context.get(path);
	}

}
