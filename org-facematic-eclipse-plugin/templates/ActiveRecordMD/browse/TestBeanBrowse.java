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
import org.facematic.sandbox.activerecord.ActiveRecordManager;
import static org.facematic.sandbox.activerecord.ActiveRecordManager.Activity.*;
import org.facematic.utils.FacematicDaoUtils;
import ${className};

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table;
import ${editFormQualifiedName};

/**
 * 
 *
 */
@FmView(name = "${browserViewQualifiedName}")
public class ${controllerSimpleName} implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(${controllerSimpleName}.class);

	@Inject
	@FmUI
	FacematicUI ui;

	@FmViewComponent(name = "tsOverlay")
	TabSheet tsOverlay;

	@FmViewComponent(name = "tabBrowse")
	Component tabBrowse;
	
	@FmViewComponent(name = "tabEdit")
	Component tabEdit;

	@FmViewComponent(name = "table")
	Table table;

	@FmViewComponent(name = "view")
	VerticalLayout view;

	@FmController(name = "editForm")
	${editFormSimpleName} editForm;

	
	private TestBeanDao dao;
	
	ActiveRecordManager<TestBean> recordManager;
	
	// from FaceProducer
	@Override
	public void prepareContext(FaceProducer producer) {
	}

    // from FaceProducer
	@Override
	public void init() {
	    dao = FacematicDaoUtils.getDao(TestBeanDao.class);
	    
		recordManager = new ActiveRecordManager<TestBean>(TestBean.class, dao, table) {
			@Override
			public void edit(TestBean bean) {
				TestBeanBrowse.this.edit(bean);
			}
		};
		recordManager.disable(MOVE);
		recordManager.retrieve ();
	}

    // from recordManager
	public void edit(TestBean bean) {
		editForm.bind (bean);
		tsOverlay.setSelectedTab(tabEdit);
	}
	
	// from editor
	public void cancelEdit () {
	   recordManager.cancelEdit ();
	   toBrowse ();
	}

    // from editor
	public void update() {
		recordManager.update();
		toBrowse ();
	}

    // from editor
	public void delete() {
		recordManager.delete();
		toBrowse();
	}
	
	// from editor
	private void toBrowse () {
	   tsOverlay.setSelectedTab(tabBrowse);
	}
}
