package org.facematic.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;

import com.vaadin.data.util.converter.Converter;

public class BeanConverter<PRESENTATION> implements Converter {
	private static final Logger logger = LoggerFactory.getLogger(BeanConverter.class);
	
	private Class<PRESENTATION> presentationClass;
	private Class<? > modelClass;
	private Method getterMethod;
	private Method setterMethod;
	

	public BeanConverter (Class<PRESENTATION> presentationClass, String fieldName)  {
		this.presentationClass = presentationClass;
		String getter="get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
		String setter="set"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
		getterMethod = null;
		
		try {
		   getterMethod = presentationClass.getMethod (getter);
		} catch (Exception e) {
			throw new RuntimeException ("Can't find getter "+presentationClass.getCanonicalName()+"."+getter);
		}
		
		Class<?> type = getterMethod.getReturnType();
		
		try {
			   setterMethod = presentationClass.getMethod (setter, type);
			} catch (Exception e) {
				throw new RuntimeException ("Can't find setter "+presentationClass.getCanonicalName()+"."+setter);
			}
		
		
		if (type == null || type.equals(Void.TYPE)) {
			throw new RuntimeException ("Invalid getter type "+presentationClass.getCanonicalName()+"."+getter+":"+type);
		}
		
		this.modelClass = type;
	}

	@Override
	public Object convertToModel(Object value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value==null)
			return null;
		try {
			return getterMethod.invoke (value);
		} catch (Exception e) {
			logger.error("Can't invoke getter "+getterMethod, e);
			return null;
		}
	}

	@Override
	public PRESENTATION convertToPresentation(Object value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value==null) {
			return null;
		}
		try {
			PRESENTATION presentation = presentationClass.newInstance();
			setterMethod.invoke (presentation, value);
			return presentation;
		} catch (Exception e) {
			logger.error("Can't invoke setter "+setterMethod, e);
			return null;
		}
	}

	@Override
	public Class<?> getModelType() {
		return modelClass;
	}

	@Override
	public Class<PRESENTATION> getPresentationType() {
		return presentationClass;
	}
	

}
