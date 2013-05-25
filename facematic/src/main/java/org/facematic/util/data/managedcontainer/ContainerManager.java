package org.facematic.util.data.managedcontainer;

import org.facematic.util.data.managedcontainer.ManagedContainerItem;

/**
 * @author "Stanislav Lipchansky"
 *
 * @param <TYPE>
 */
public interface ContainerManager<TYPE> {
	void edit    (ManagedContainerItem<TYPE> bean);
	void delete  (ManagedContainerItem<TYPE> bean);
	void editNull ();
	TYPE createEmpty ();
	
	boolean canMove ();
	boolean canDelete ();
	boolean canEdit ();
	boolean canCreate ();
}
