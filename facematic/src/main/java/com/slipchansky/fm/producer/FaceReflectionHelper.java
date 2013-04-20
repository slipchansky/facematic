package com.slipchansky.fm.producer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.slipchansky.fm.mvc.annotations.FmViewComponent;
import com.slipchansky.fm.mvc.annotations.FmController;
import com.vaadin.ui.Component;

/**
 * Helps to modify controller fields according to markup interpretation
 * @author stas
 * 
 * @param <T>
 */
interface FieldAnnotationMatcher<T extends Annotation> {
	boolean match(Field field, T annotation, String value);
}

public class FaceReflectionHelper {
	private static final String FIELD_MODIFIERS = "modifiers";
	private Object instance;
	private Field[] fields;
	 
	private final static FieldAnnotationMatcher<FmController> controllerMatcher = new FieldAnnotationMatcher<FmController>() {
		@Override
		public boolean match(Field field, FmController annotation, String value) {
			return (annotation.name().equals(value) || field.getName().equals(value));
		}
	};

	private final static FieldAnnotationMatcher<FmViewComponent> viewComponentMatcher = new FieldAnnotationMatcher<FmViewComponent>() {
		@Override
		public boolean match(Field field, FmViewComponent annotation, String value) {
			return (annotation.name().equals(value) || field.getName().equals(value));
		}
	};

	public FaceReflectionHelper(Object instance) {
		this.instance = instance;
		if (instance != null) {
			Class<? extends Object> instanceClass = instance.getClass();
			fields = instanceClass.getDeclaredFields();
			for (Field f : fields) {
				updateFieldModifiers(f);
			}
		}
	}

	/**
	 * sets properly annotated container attribute to reference on view component instance  
	 * @param name
	 * @param view
	 */
	public void putView(Object name, Component view) {
		if (instance == null) {
			return;
		}

		Field field = findAnnotatedField((String) name, viewComponentMatcher, FmViewComponent.class);
		if (field != null) {
			setFieldValue(field, view);
		}
	}

	/**
	 * saves named controller instance reference to parent controller property
	 * @param name
	 * @param controller
	 */
	public void putController(String name, Object controller) {
		if (instance == null) {
			return;
		}
		
		Field field = findAnnotatedField(name, controllerMatcher,
				FmController.class);
		if (field != null) {
			setFieldValue(field, controller);
		}

	}

	/**
	 * just for testing purposes
	 * 
	 * @param key
	 * @param value
	 */
	public void putString(Object key, String value) {
		if (instance == null) {
			return;
		}

		Field field = findAnnotatedField((String) key, viewComponentMatcher,
				FmViewComponent.class);
		if (field != null) {
			setFieldValue(field, value);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Field findAnnotatedField(String value, FieldAnnotationMatcher matcher,
			Class<? extends Annotation> annotationClass) {
		for (Field field : fields) {
			Annotation a = field.getAnnotation(annotationClass);
			if (a == null) {
				continue;
			}
			if (matcher.match(field, a, value)) {
				return field;
			}
		}
		return null;

	}

	void updateFieldModifiers(Field field) {
		try {
			Field modifiersField;
			modifiersField = Field.class.getDeclaredField(FIELD_MODIFIERS);
			modifiersField.setAccessible(true);
			int modifiers = modifiersField.getInt(field);
			modifiers &= ~Modifier.FINAL;
			modifiers &= ~Modifier.PRIVATE;
			modifiers &= ~Modifier.PROTECTED;
			modifiers |= Modifier.PUBLIC;
			modifiersField.setInt(field, modifiers);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}

	}

	private void setFieldValue(Field field, Object value) {
		try {
			field.set(instance, value);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}

}
