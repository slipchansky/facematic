package org.facematic.core.ui;

import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.producer.builders.TabSheetBuilder;

import com.vaadin.ui.UI;

/**
 * Base class for building facematic ui.
 * Contains mechanisms for instantinate annotated beans
 * 
 * @author papa
 *
 */
public abstract class FacematicUI extends UI {
	private final static Logger logger = LoggerFactory.getLogger(FacematicUI.class);

	private static final String BEAN_MANAGER_URI = "java:comp/BeanManager";
	protected FaceProducer producer;
	
	@Inject BeanManager beanManager;
	boolean beanManagerDoesNotExists = false;
	
	
	public FacematicUI () {
		producer =new FaceProducer (this, this);
	}

	/**
	 * Builds bean instance with injections 
	 * 
	 * @param clazz
	 * @return
	 */
	public Object getClassInstance(Class clazz) {
		if (!isThereBeanManager()) {
			logger.trace ("Bean manager not found.");
			return null;
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
      	       return instance;
    	   } catch (Exception e) {
    		    // skip
    	   }
    	}
		return null;
	}
	

	/**
	 * Checks for BeanManager existence 
	 * @return
	 */
	private boolean isThereBeanManager() {
    	
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
