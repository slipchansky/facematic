package org.facematic.utils;

import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;

public class FacematicCdiUtils {
	private static final Logger logger = LoggerFactory.getLogger(FacematicCdiUtils.class);
	private static final String BEAN_MANAGER_URI = "java:comp/BeanManager";

	 
	static BeanManager beanManager;
	static boolean     beanManagerDoesNotExists = false;
	
	/**
	 * Builds bean instance with injections 
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T>T getClassInstance(Class<T> clazz) {
		if (!isThereBeanManager()) {
			logger.trace ("Bean manager not found.");
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				logger.error("Can't create class instance "+clazz.getCanonicalName(), e);
				return null;
			}
		}
		
		Set<Bean<?>> beans = beanManager.getBeans(clazz, new AnnotationLiteral<Any>() {});
		if (beans == null) {
			logger.trace ("BeanManager cannot get beans "+clazz.getCanonicalName());
			return null;
		}
		
    	for (Bean<?> bean : beans) {
    		if (clazz != bean.getBeanClass()) {
    			continue;
    		}
    	   try {
      	       Object instance = beanManager.getReference(bean, clazz, beanManager.createCreationalContext(bean));
      	       logger.trace ("BeanManager gave reference of "+clazz.getCanonicalName());
      	       return (T)instance;
    	   } catch (Exception e) {
    		    // skip
    	   }
    	}
		return null;
	}
	
	
	private static boolean isThereBeanManager() {
    	
        if (beanManager == null) {
        	if (beanManagerDoesNotExists) {
        		return false;
        	}
        	
            try {
                InitialContext initialContext = new InitialContext();
                beanManager = (BeanManager) initialContext.lookup(BEAN_MANAGER_URI);
            } catch (NamingException e) {
            	logger.trace("Could not find BeanManager at "+BEAN_MANAGER_URI);
                beanManagerDoesNotExists = true;
            }
        }
        
        return !beanManagerDoesNotExists;
    }
	
	
	

}
