package org.facematic.ext.activerecord;

import java.util.Collection;

import org.facematic.core.nvo.FacematicDao;
import org.facematic.util.data.managedcontainer.ContainerManager;
import org.facematic.util.data.managedcontainer.ManagedContainer;
import org.facematic.util.data.managedcontainer.ManagedContainerItem;

import com.vaadin.ui.Table;

public abstract class ActiveRecordManager<BEANTYPE> implements ContainerManager<BEANTYPE> {
	
	public enum Activity {
		CREATE, EDIT, MOVE, DELETE  
	}
	
	private boolean canCreate = true;
	private boolean canEdit = true;
	private boolean canMove = false;
	private boolean canDelete = true;
	private ManagedContainer<BEANTYPE> managedContainer;
	private FacematicDao<BEANTYPE> dao;
	private ManagedContainerItem<BEANTYPE> activeRecord;
	private Table table;
	
	
	public ActiveRecordManager (Class<BEANTYPE> clazz) {
		managedContainer = new ManagedContainer<BEANTYPE>(clazz);
		managedContainer.setManager(this);
	}
	
	public ActiveRecordManager (Class<BEANTYPE> clazz, FacematicDao<BEANTYPE> dao) {
		this(clazz);
		this.dao = dao;
	}
	
	public ActiveRecordManager (Class<BEANTYPE> clazz, FacematicDao<BEANTYPE> dao, Table table) {
		this (clazz, dao);
		bind (table);
	}
	
	public void bind (Table table) {
		managedContainer.bind (this.table=table);
	}
	
	public void setData (Collection<BEANTYPE> data) {
		managedContainer.setData(data);
	}
	
	public void retrieve () {
		if (dao==null) 
			return;
		setData (dao.retrieve());
	}
	
	
	public void enable (Activity ... activities) {
		setEnabled (true, activities);
	}
	
	public void disable (Activity ... activities) {
		setEnabled (false, activities); 
	}

	private void setEnabled(boolean on, Activity[] activities) {
		if (activities==null || activities.length==0) {
			return;
		}
		
		for (Activity a : activities) {
			switch (a) {
			  case CREATE:
				  canCreate = on;
				  break;
			  case DELETE:
				  canDelete = on;
				  break;
			  case EDIT:
				  canEdit = on;
				  break;
			  case MOVE:
				  canMove = on;
				  break;
			}
		}
		applyChanges ();
	}


	@Override
	public void edit(ManagedContainerItem<BEANTYPE> item) {
		activeRecord = item;
		edit (item.getBean());
	}
	
	public void update () {
		if (activeRecord==null) {
			return;
		}
		if ( activeRecord.isNewInstance() ) {
			dao.create(activeRecord.getBean());
		} else {
			dao.update(activeRecord.getBean());
		}
		managedContainer.commit();
	}
	
	public void cancelEdit () {
		if (activeRecord.isNewInstance()) {
			managedContainer.killItem (activeRecord);
			if (table!=null)
				table.select(managedContainer.getNewItem());
		}
		managedContainer.commit();
	}

	public abstract void edit(BEANTYPE bean);

	@Override
	public boolean delete(ManagedContainerItem<BEANTYPE> item) {
		return dao.delete(item.getBean ());
	}
	
	public boolean delete () {
		if (activeRecord == null)
			return false;
		return managedContainer.removeItem(activeRecord);
	}

	@Override
	@Deprecated
	public void blank() {
	}

	@Override
	public BEANTYPE createEmpty() {
		return dao.newInstance();
	}

	@Override
	public final boolean canMove() {
		return canMove;
	}

	@Override
	public final boolean canDelete() {
		return canDelete;
	}

	@Override
	public final boolean canEdit() {
		return canEdit;
	}

	@Override
	public final boolean canCreate() {
		return canCreate;
	}
	
	private void applyChanges() {
	}
}
