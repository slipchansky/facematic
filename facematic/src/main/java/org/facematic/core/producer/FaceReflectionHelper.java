package org.facematic.core.producer;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.annotations.FmController;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.servlet.FacematicServlet;
import org.facematic.core.ui.FacematicUI;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * Helps to modify controller fields according to markup interpretation
 * @author stas
 * 
 * @param <T>
 */
interface FieldAnnotationMatcher<T extends Annotation> {
	boolean match(Field field, T annotation, String value);
}

public class FaceReflectionHelper implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(FaceReflectionHelper.class);
	
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
	
	private final static FieldAnnotationMatcher<Inject> injectComponentMatcher = new FieldAnnotationMatcher<Inject>() {
		@Override
		public boolean match(Field field, Inject annotation, String value) {
			String typeName = field.getType().getCanonicalName();
			return value.equals (typeName);
			
		}
	};
	
	
	private final static FieldAnnotationMatcher<FmUI> uiInjectionMatcher = new FieldAnnotationMatcher<FmUI>() {
		@Override
		public boolean match(Field field, FmUI annotation, String value) {
			return true;
		}
	};

	public FaceReflectionHelper(Object instance) {
		this.instance = instance;
		if (instance != null) {
			Class<? extends Object> instanceClass = instance.getClass();
			fields = instanceClass.getDeclaredFields();
//			for (Field f : fields) {
//				updateFieldModifiers(f);
//			}
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
	private Field findAnnotatedField(String value, FieldAnnotationMatcher matcher, Class<? extends Annotation> annotationClass) {
		for (Field field : fields) {
			Annotation a = field.getAnnotation(annotationClass);
			if (a == null) {
				continue;
			}
			if (matcher.match(field, a, value)) {
				return field;
			}
		}
		logger.trace("Annotated field not found @"+annotationClass.getSimpleName()+"("+value+")");
		return null;

	}

	private static void updateFieldModifiers(Field field) {
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
			logger.error ("Could not open access to "+field.getClass().getCanonicalName()+"."+field.getName());
			throw (new RuntimeException(e));
		}

	}

	private void setFieldValue(Field field, Object value) {
		try {
			updateFieldModifiers(field); // moved from public FaceReflectionHelper(Object instance) becuuse of jrebel issues 
			field.set(instance, value);
		} catch (Exception e) {
			//logger.error ("Could not set value "+field.getClass().getCanonicalName()+"."+field.getName()+"="+value);
			throw (new RuntimeException(e));
		}
	}

	@Deprecated
	public void addUiInjections (UI ui) {
		if (instance==null) {
			return;
		}
		
		Field uiField = findAnnotatedField("ui", uiInjectionMatcher, FmUI.class);
		if (uiField != null) {
			setFieldValue(uiField, ui);
		}
		
		Field[] uiFields = ui.getClass().getDeclaredFields();
		
		for (Field f : uiFields) {
			Inject injectAnnotation = f.getAnnotation(Inject.class);
			
			if (injectAnnotation != null) {
				String key = f.getType().getCanonicalName();
				Field annotatedField = findAnnotatedField(key, injectComponentMatcher, Inject.class);
				if (annotatedField != null) {
					Object value = getFieldValue (ui, f);
					setFieldValue (annotatedField, value);
				}
			}
		}
		
		
	}

	public static Object getFieldValue(Object instance, Field field) {
		updateFieldModifiers(field);
		try {
			return field.get (instance);
		} catch (Exception e) {
			logger.error ("Could not get value from "+field.getClass().getCanonicalName()+"."+field.getName());
			return null;
		}
	}

}
