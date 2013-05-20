package org.facematic.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FacematicBeanUtils {

	public static List getFakeDataList(Class beanClass, int size) {
		List result = new ArrayList();
		for (int i = 0; i < size; i++) {
			Object bean = getFakeBean(beanClass, "" + i);
			result.add(bean);
		}
		return result;
	}

	private static Object beanPreset(Object instance, String suffix) {
		if (instance == null)
			return null;

		Class clazz = instance.getClass();
		Method methods[] = clazz.getMethods();

		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (!m.getName().startsWith("set"))
				continue;

			Class<?>[] types = m.getParameterTypes();
			if (types.length != 1)
				continue;

			Object value = null;
			if (suffix != null) {
				if (types[0] == String.class) {
					if (suffix != null) {
						value = m.getName().substring(3) + " " + suffix;
					} else
						value = "";
				} else if (types[0] == Integer.class
						|| types[0] == Integer.TYPE) {
					value = new Integer(suffix);
				} else if (types[0] == Float.class || types[0] == Float.TYPE) {
					value = new Float(suffix);
				} else if (types[0] == Double.class || types[0] == Double.TYPE) {
					value = new Double(suffix);
				} else if (types[0] == Date.class) {
					value = new Date();
				}
			} else if (types[0] == String.class)
				value = "";

			if (value != null) {
				try {
					m.invoke(instance, value);
				} catch (Exception e) {
					// skip
				}
			}
		}
		return instance;
	}

	public static <T> T getFakeBean(Class<T> clazz, String suffix) {
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
		return (T) beanPreset(instance, suffix);
	}

	public static <T> T emptyBean(Object bean) {
		return (T) beanPreset(bean, null);
	}

}
