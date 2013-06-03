package org.facematic.site.showcase.databinding;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmController;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.utils.FacematicBeanUtils;
import org.facematic.utils.FacematicUtils;
import org.facematic.utils.FormBinder;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.databinding.DataBindingBean;

/**
 *
 */
@FmView(name = "org.facematic.site.showcase.databinding.DataBindingExample")
@ShowCase(caption = "Data binding", description = "How to bind data on form", moreClasses={
DataBindingBean.class,
ComboReference.class,
ComboEnum.class
})
public class DataBindingExample implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(DataBindingExample.class);
	
	protected FormBinder<DataBindingBean> binder;  

	@Inject
	@FmUI
	protected FacematicUI ui;
	
    @FmViewComponent(name="comboEnum")
    @PropertyId("comboEnum")
    ComboBox comboEnum;

    @FmViewComponent(name="comboInt")
    @PropertyId("comboInt")
    ComboBox comboInt;

    @FmViewComponent(name="date")
    @PropertyId("date")
    DateField date;

    @FmViewComponent(name="string")
    @PropertyId("string")
    TextField string;



	/**
	 *
	 */
	@Override
	public void prepareContext(FaceProducer producer) {
	}

	/**
	 *
	 */
	@Override
	public void init() {
	    List<ComboReference> reference = (List<ComboReference>)FacematicBeanUtils.getFakeDataList(ComboReference.class, 10);
	    FacematicUtils.setComboBoxData(comboInt, ComboReference.class, reference, "id", "name");
        binder = new FormBinder (this);
        
        DataBindingBean bean = new DataBindingBean ();
        bean.setComboEnum(ComboEnum.TWO);
        bean.setComboInt(2);
        bean.setString("Initial String");
        bean.setDate(new Date ());
        
        binder.bind (bean);
	}
	
	



	@FmReaction("Button.onClick")
	public void save(ClickEvent event) {

		if (!binder.isValid()) {
			Notification.show("Wrong data entered", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		try {
			binder.commit ();
		} catch (CommitException e) {
			Notification.show ("Can't save data", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		DataBindingBean bean = binder.getBean();
		Notification.show ("Result is", bean.toString(), Notification.Type.TRAY_NOTIFICATION);
		
	}

}
