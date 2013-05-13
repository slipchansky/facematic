package org.facematic.core.ui;

import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.ui.UI;

/**
 * Base class for building facematic ui.
 * Contains mechanisms for instantinate annotated beans
 * 
 * @author papa
 *
 */
public abstract class FacematicUI extends UI {
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
		if (!isThereBeanManager())
			return null;
		
		Set<Bean<?>> beans = beanManager.getBeans(clazz, new AnnotationLiteral<Any>() {});
		if (beans == null) {
			return null;
		}
		
    	for (Bean<?> bean : beans) {
    		if (clazz != bean.getBeanClass()) {
    			continue;
    		}
    	   try {
      	       Object instance = beanManager.getReference(bean, clazz, beanManager.createCreationalContext(bean));
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
                beanManagerDoesNotExists = true;
            }
        }
        
        return !beanManagerDoesNotExists;
    }

}
