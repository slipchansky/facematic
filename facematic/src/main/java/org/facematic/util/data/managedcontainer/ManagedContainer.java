package org.facematic.util.data.managedcontainer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.util.data.managedcontainer.Action;
import org.facematic.util.data.managedcontainer.ContainerManager;
import org.facematic.util.data.managedcontainer.ManagedContainer;
import org.facematic.util.data.managedcontainer.ManagedContainerItem;
import org.facematic.utils.FacematicUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.ui.Table;

/**
 * @author "Stanislav Lipchansky"
 * 
 *         <pre>
 * public class Test implements FmBaseController, ContainerManager {
 *  ...
 *       FmManagedContainer ds = new FmManagedContainer(SomeBean.class, data);
 *       ds.bind(table);
 *       ds.setManager(this);
 *  }
 * 
 * 	public void edit(ManagedContainerItem bean) {
 * 	    editor.setValue(bean.getBean());
 * 	}
 * 
 * 	public void clear() {
 * 	     editor.setValue(null);
 * 	}
 * 
 * 	public void delete(ManagedContainerItem bean) {
 * 	}
 * 
 * 	public boolean canMove() {
 * 		return true;
 * 	}
 * 
 * 	public boolean canDelete() {
 * 		return false;
 * 	}
 * 
 * 	public boolean canEdit() {
 * 		return false;
 * 	}
 * </pre>
 * 
 * 
 * @param <BEANTYPE>
 */
public class ManagedContainer<BEANTYPE> extends
		BeanItemContainer<ManagedContainerItem> {

	private static final Logger logger = LoggerFactory
			.getLogger(ManagedContainer.class);

	ContainerManager<BEANTYPE> manager = null;

	class Accessor {
		PropertyDescriptor propertyDescriptor;

		public Accessor(PropertyDescriptor d) {
			super();
			propertyDescriptor = d;
		}

		public Class getPropertyType() {
			return propertyDescriptor.getPropertyType();
		}

		public Method getGetter() {
			return propertyDescriptor.getReadMethod();
		}

		public Method getSetter() {
			return propertyDescriptor.getWriteMethod();
		}

		public Object get(Object instance) {
			try {
				return propertyDescriptor.getReadMethod().invoke(instance);
			} catch (Exception e) {
				logger.error("getter invocation error", e);
				return null;
			}
		}

		public void set(Object instance, Object value) {
			try {
				propertyDescriptor.getWriteMethod().invoke(instance, value);
			} catch (Exception e) {
				logger.error("setter invocation error", e);
			}
		}
	}

	Map<String, Accessor> accessors = new HashMap();
	private Class beanClass;
	private Table table;
	private ManagedContainerItem createItem;
	private ManagedContainerItem currentItem;

	public ManagedContainer(Class beanClass, Collection<BEANTYPE> collection)
			throws IllegalArgumentException {
		super(ManagedContainerItem.class);
		this.beanClass = beanClass;
		prepareModel(beanClass);
		setData(collection);

	}

	public ManagedContainer(Class type) throws IllegalArgumentException {
		super(ManagedContainerItem.class);
		this.beanClass = type;
		prepareModel(type);
		implementCreate();
		
	}

	/**
	 * @param collection
	 */
	public void setData(Collection<BEANTYPE> collection) {
		List<ManagedContainerItem> data = new ArrayList<ManagedContainerItem>();
		removeAllItems();
		for (BEANTYPE o : collection) {
			ManagedContainerItem<BEANTYPE> bean = new ManagedContainerItem<BEANTYPE>(o, this);
			applyManagerActions(bean);
			if (manager == null)
				bean.getControls().hideAction(Action.EDIT);
			data.add(bean);
		}
		addAll(data);
		implementCreate();
		
		if (table != null)
			table.refreshRowCache();
	}

	private void implementCreate() {
		try {
			if (manager == null) {
				return;
			}
			if (!manager.canCreate()) {
				return;
			}

			if (createItem != null && createItem.getBean() != null) {
				applyManagerActions(createItem);
			}

			if (createItem == null || createItem.getBean() != null) {
				createItem = new ManagedContainerItem<BEANTYPE>(null, this);
				logger.trace("if (createItem == null || createItem.getBean()!=null)");
			}

			if (indexOfId(createItem) >= 0) {
				super.removeItem (createItem);
				createItem.setBean(null);
			}

			addItem(createItem);
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	/**
	 * @param beanClass
	 */
	private void prepareModel(Class beanClass) {
		accessors = getPropertyAccessors(beanClass);
		Map<String, Accessor> manageAccessors = getPropertyAccessors(ManagedContainerItem.class);
		accessors.put("controls", manageAccessors.get("controls"));
		accessors.put("bean", manageAccessors.get("bean"));
	}

	/**
	 * @param beanClass
	 * @return
	 */
	private Map<String, Accessor> getPropertyAccessors(Class beanClass) {
		Map<String, Accessor> accessors = new HashMap<String, Accessor>();
		try {
			BeanInfo info = Introspector.getBeanInfo(beanClass);
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
			for (PropertyDescriptor d : descriptors) {
				if ("class".equals(d.getName())) {
					continue;
				}
				accessors.put(d.getName(), new Accessor(d));
			}

		} catch (IntrospectionException e) {
			logger.error("can't get bean info for "
					+ beanClass.getCanonicalName());
		}
		return accessors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.util.AbstractBeanContainer#getContainerPropertyIds()
	 */
	@Override
	public Collection<String> getContainerPropertyIds() {
	    Set<String> propertyIds = accessors.keySet();
		return propertyIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.util.BeanItemContainer#addItem(java.lang.Object)
	 */
	@Override
	public BeanItem<ManagedContainerItem> addItem(Object item) {
		if (item == null) {
			return null;
		}

		ManagedContainerItem controlled = (item instanceof ManagedContainerItem) ? (ManagedContainerItem) item : new ManagedContainerItem(item, this);
		if (manager == null) {controlled.getControls().hideAction(Action.EDIT);
		} else {
			applyManagerActions(controlled);
		}
		return internalAddItemAtEnd(controlled, createBeanItem(controlled),
				true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.util.AbstractBeanContainer#getContainerProperty(java.
	 * lang.Object, java.lang.Object)
	 */
	@Override
	public Property getContainerProperty(Object itemId, final Object propertyId) {

		if (itemId instanceof ManagedContainerItem) {
			final ManagedContainerItem controlled = (ManagedContainerItem) itemId;
			final Object instance = controlled.getBean();
			final Accessor accessor = accessors.get((String) propertyId);
			Object val = null;
			
			if ("controls".equals(propertyId)) {
				return super.getContainerProperty(itemId, propertyId);
			}

			if ("bean".equals(propertyId)) {
				if (beanClass == String.class) {
					val = instance;
					if (controlled == createItem) {
						val = "( . . . )";
					}
				}

			}

			if ((!"controls".equals(propertyId) && !"bean".equals(propertyId) && accessor != null)
					|| (val != null )) {

				try {

					if (val == null) {
						if (instance == null) {
							if (itemId == createItem
									&& accessor.getPropertyType().equals(
											String.class)) {
								val = "( . . . )";
							} else
								return null;
						} else {
							if (accessor.getGetter() == null) {
								return null;
							}
							val = accessor.get(instance);
						}

						if (val == null) {
							return null;
						}
					}

					final Object value = val;

					return new Property() {

						@Override
						public Object getValue() {
							return value;
						}

						@Override
						public void setValue(Object newValue)
								throws ReadOnlyException {
							if (beanClass == String.class) {
								controlled.setBean((String) newValue);
							} else
								accessor.set(instance, newValue);
						}

						@Override
						public Class getType() {
							return beanClass==String.class?String.class:accessor.getPropertyType();
						}

						@Override
						public boolean isReadOnly() {
							if (beanClass == String.class)
								return false;
							return accessor.getSetter() == null;
						}

						@Override
						public void setReadOnly(boolean newStatus) {
							throw new RuntimeException(
									"You must implement setter for "
											+ getType().getCanonicalName()
											+ '.' + propertyId);
						}
					};
				} catch (Exception e) {
					logger.error("error", e);
				}
			}
		}

		return super.getContainerProperty(itemId, propertyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.util.AbstractBeanContainer#getType(java.lang.Object)
	 */
	@Override
	public Class<?> getType(Object propertyId) {
		try {
		return accessors.get(propertyId).getPropertyType();
		} catch (Exception e) {
			return Object.class;
		}
	}

	/**
	 * @param item
	 */
	public void edit(ManagedContainerItem item) {
		currentItem = item;
		if (manager != null) {
			if (table != null) {
				if (item == createItem) {
					item.setBean(manager.createEmpty());
					item.setIsNewInstance (true);
				}
				else 
					item.setIsNewInstance (false);
				table.select(item);
			}
			manager.edit(item);
		}
	}

	/**
	 * @param item
	 */
	public void moveDown(ManagedContainerItem<BEANTYPE> item) {
		currentItem = item;
		if (createItem != null)
			super.removeItem(createItem);
		try {
			List<ManagedContainerItem> items = getItemIds();
			int index = items.indexOf(item);

			if (index == items.size() - 1) {
				removeItem(item);
				addItemAt(0, item);
			} else {
				removeItem(item);
				addItemAt(index + 1, item);
			}
			if (table != null) {
				table.refreshRowCache();
				table.select(item);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		implementCreate();
	}

	/**
	 * @param item
	 */
	public void moveUp(ManagedContainerItem item) {
		if (createItem != null)
			super.removeItem(createItem);
		try {
			List<ManagedContainerItem> items = getItemIds();
			int index = items.indexOf(item);

			removeItem(item);
			if (index > 0) {
				addItemAt(index - 1, item);
			} else {
				addItem(item);
			}

			if (table != null) {
				table.refreshRowCache();
				table.select(item);
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		implementCreate();
	}

	/**
	 * @param item
	 */
	public void remove(ManagedContainerItem item) {
		if (item != null) {
			removeItem(item);
			if (manager != null) {
				manager.blank();
			}
			if (table != null) {
				table.refreshRowCache();
				table.select(null);
			}
		}
	}

	/**
	 * 
	 */
	public void addNew() {
		if (manager == null) {
			return;
		}
		BEANTYPE bean = manager.createEmpty();
		BeanItem<ManagedContainerItem> itemId = addItem(bean);
		manager.edit(itemId.getBean());
	}

	/**
	 * @param table
	 */
	public void bind(Table table) {
		this.table = table;
		table.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				implementCreate();
			}
		});
		implementCreate();
		FacematicUtils.setTableDatasource(table, this);
		table.refreshRowCache();
	}

	/**
	 * @param editor
	 */
	public void setManager(ContainerManager<BEANTYPE> editor) {
		this.manager = editor;
		
		if (indexOfId(createItem) >= 0) {
			super.removeItem (createItem);
			createItem = null;
		}
		
		List<ManagedContainerItem> allItems = getItemIds();
		for (ManagedContainerItem b : allItems) {
			applyManagerActions(b);
		}
		implementCreate();
	}

	/**
	 * @param b
	 */
	private void applyManagerActions(ManagedContainerItem b) {

		if (manager == null) {
			b.getControls().hideAction(Action.EDIT);
			b.getControls().hideAction(Action.DELETE);
			return;
		}

		if (b.getBean() == null) {
			b.getControls().showAction(Action.EDIT);
			b.getControls().hideAction(Action.UP);
			b.getControls().hideAction(Action.DOWN);
			b.getControls().hideAction(Action.DELETE);
			return;
		}
		

		if (manager.canEdit())
			b.getControls().showAction(Action.EDIT);
		else
			b.getControls().hideAction(Action.EDIT);
		if (manager.canMove()) {
			b.getControls().showAction(Action.UP);
			b.getControls().showAction(Action.DOWN);
		} else {
			b.getControls().hideAction(Action.UP);
			b.getControls().hideAction(Action.DOWN);
		}
		if (manager.canDelete()) {
			b.getControls().showAction(Action.DELETE);
		} else {
			b.getControls().hideAction(Action.DELETE);
		}
	}

	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		if (createItem != null) {
			super.removeItem(createItem);
		}
		super.sort(propertyId, ascending);
		implementCreate();
	}

	@Override
	public boolean removeItem(Object itemId) {
		if (itemId == null) {
			return false;
		}
		
		if (beanClass.isAssignableFrom(itemId.getClass()) ) {
			if (currentItem != null && currentItem.getBean ()==itemId) {
				itemId = currentItem;
			} 
			else {
				itemId = findItemForBean (itemId);
			}
			if (itemId == null) {
				return false;
			}
		}
		
		if (itemId == createItem) {
			return false;
		}
		
		if (manager != null) {
			if (!manager.delete((ManagedContainerItem)itemId)) {
				return false;
			}
		}
		
		boolean result = super.removeItem(itemId);
		if (result) {
			currentItem = null;
			if (table != null) {
				table.refreshRowCache();
			}
		}
		return result;
	}

	public Object findItemForBean(Object itemId) {
		for (ManagedContainerItem i : getAllItemIds()) {
			if (i.getBean() == itemId) 
				return i;
		}
		return null;
	}

	public void commit() {
		if (createItem != null)
		if (createItem.getBean() != null) {
			applyManagerActions(createItem);
		} 
		implementCreate();
		if (table != null) {
			table.refreshRowCache();
		}
	}

	public ManagedContainerItem<BEANTYPE> getNewItem() {
		if (createItem != null) return createItem;
		implementCreate();
		return createItem;
	}

	public void clear() {
		removeAllItems ();
		implementCreate();
	}

	public List<BEANTYPE> getData() {
		List<BEANTYPE> result = new ArrayList<BEANTYPE>();
		List<ManagedContainerItem> allItems = getItemIds();
		
		for (ManagedContainerItem item : allItems) {
			if (item.getBean()==null) {
				continue;
			}
			result.add((BEANTYPE)item.getBean ());
		}
		return result;
	}
	
	public void killItem  (Object itemId) {
		super.removeItem(itemId);
		implementCreate();
		if (table != null)
			table.refreshRowCache();
		
	}


}
