package org.facematic.site.showcase.table;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.annotations.ShowFiles;
import org.facematic.utils.FacematicBeanUtils;
import org.facematic.utils.FacematicUtils;

import com.vaadin.data.util.BeanItemContainer;


@FmView(name="org.facematic.site.showcase.table.TableExample")
@ShowCase(caption = "Table", description = "Vaadin Table implementation", moreClasses={TableRowBean.class})
@ShowFiles(java = true, xml = true)
public class TableExample implements FmBaseController {

	private static final Logger logger = LoggerFactory.getLogger(TableExample.class);

    @FmViewComponent(name="tableView")
    com.vaadin.ui.Table tableView;

    @Override
	public void prepareContext(FaceProducer producer) {
	}
	
    @Override
    public void init () {
    	BeanItemContainer<TableRowBean> dataSource = new BeanItemContainer<TableRowBean> (TableRowBean.class, FacematicBeanUtils.getFakeDataList(TableRowBean.class, 10));
    	FacematicUtils.setTableDatasource(tableView, dataSource);
    }
	
}

