package ${controllerPackageName};

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmController;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.utils.FormBinder;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import ${className};


/**
 *
 */
@FmView(name = "${editViewQualifiedName}")
public class ${editFormSimpleName} implements FmBaseController {
	private static final Logger logger = LoggerFactory.getLogger(${editFormSimpleName}.class);
	
	protected FormBinder binder;  

	@Inject
	@FmUI
	protected FacematicUI ui;
	
 ${editorContollerFields}

    @FmViewComponent(name="form")
    protected FormLayout form;

    @FmViewComponent(name="buttonSave")
    protected Button buttonSave;

    @FmViewComponent(name="buttonDelete")
    protected Button buttonDelete;

    @FmViewComponent(name="view")
    protected VerticalLayout view;
    
    @FmController
    protected ${controllerSimpleName} parent;

	@Override
	public void prepareContext(FaceProducer producer) {
	}

	@Override
	public void init() {
        binder = new FormBinder (this);
	}
	
    // from browser
	public void bind (${classSimpleName} bean) {
		binder.bind (bean);
	}

    // from button
	public void update () {
		if (!binder.isValid()) {
			Notification.show("Entered data is invalid", Notification.Type.ERROR_MESSAGE);
			return;
		}
		try {
			binder.commit ();
			parent.update ();
		} catch (CommitException e) {
			Notification.show("Can't commit data", Notification.Type.ERROR_MESSAGE);
		}
	}
	
    // from button
    public void delete () {
        parent.delete(); 
    }
    
    // from button
    public void cancelEdit () {
        parent.cancelEdit ();
    }
}
