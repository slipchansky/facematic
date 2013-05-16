package org.facematic.core.mvc;

import java.io.Serializable;
import java.util.Map;

import org.facematic.core.producer.FaceProducer;

/**
 * @author "Stanislav Lipchansky"
 * 
 */
public interface FmBaseController extends Serializable {

	/**
	 * <pre>
     * Implement prepareContext for put data into producer context before view building. For exampe:
	 * {@code @Override}
	 * public void prepareContext (FaceProducer producer) {
	 *     producer.put ("valueName", someValue);  
	 * }
	 * </pre>
	 * @param producer
	 */
	void prepareContext(FaceProducer producer);

	void init();
}
