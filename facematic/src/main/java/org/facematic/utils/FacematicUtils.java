package org.facematic.utils;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class FacematicUtils {
	
	public static void setTableDatasource (Table table, Container container ){
		Object[] visible = table.getVisibleColumns();
		table.setContainerDataSource(container);
		table.setVisibleColumns(visible);
	}

}
