package org.facematic.util.data.managedcontainer;

import org.facematic.util.data.managedcontainer.ManagedContainerItem;

/**
 * @author "Stanislav Lipchansky"
 *
 * @param <TYPE>
 */
public interface ContainerManager<TYPE> {
	/**
	 * implement "edit" for binding bean to editor form
	 * @param bean
	 */
	void edit    (ManagedContainerItem<TYPE> bean);
	
	/**
	 * implement "delete" for physical removing item.getBean() from perrsistence.
	 * must return true if physical removing were successful, otherwise - false. 
	 * @param bean
	 */
	boolean delete  (ManagedContainerItem<TYPE> item);
	
	/**
	 * implement edit blank in cases wnen browser and editor are visible simultinously. 
	 * blank must bind new empty bean where there are no selected rows in the browser.
	 */
	void blank ();
	
	/**
	 * must return new instance of bean when fired editing of "new" row. 
	 * @return
	 */
	TYPE createEmpty ();
	
	/**
	 * must return true if rows moving (up, down) is applicable for browsed items.
	 * @return
	 */
	boolean canMove ();
	
	/**
	 * must we show "delete" icon in browsing?
	 * @return
	 */
	boolean canDelete ();
	
	/**
	 * must we show "edit" icon in browsing?
	 * @return
	 */
	boolean canEdit ();
	
	/**
	 * must we add "new" row to the browsing ?
	 * @return
	 */
	boolean canCreate ();
}
