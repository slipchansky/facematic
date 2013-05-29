package org.facematic.utils;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class FormBinder {
	private Object fieldsContainerdInstance;
	private FieldGroup fieldGroup;
	private Object dataBean;

	public FormBinder (Object fieldsContainerdInstance) {
		this.fieldsContainerdInstance = fieldsContainerdInstance;
	}
	
	public void bind (Object dataBean) {
		attach(dataBean);
		this.fieldGroup.bindMemberFields(fieldsContainerdInstance);
	}

	public void attach(Object dataBean) {
		BeanItem beanItem = new BeanItem(dataBean);
		this.fieldGroup = new FieldGroup(beanItem);
		this.dataBean = dataBean;
	}
	
	public boolean isValid () {
		return fieldGroup.isValid();
	}
	
	public <T> T commit () throws CommitException {
		fieldGroup.commit ();
		return (T) dataBean;
	}
	
	public <T> T getBean () {
		return (T)dataBean;
	}
	
	public FieldGroup getFieldGroup () {
		return fieldGroup;
	}

}
