package org.facematic.core.producer.builders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;

import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.producer.FaceProducer;
import com.vaadin.server.Resource;
import com.vaadin.ui.Table;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class TableBuilder extends SelectBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TableBuilder.class);

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.SelectBuilder#getBuildingClass()
	 */
	@Override
	public Class getBuildingClass() {
		return Table.class;
	}

	/* (non-Javadoc)
	 * @see org.facematic.core.producer.builders.SelectBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object oComponent,	Element configuration) {
		super.build(builder, oComponent, configuration);
		Table table = (Table) oComponent;

		buildTable(builder, table, configuration);

	}

	/**
	 * @param builder
	 * @param table
	 * @param configuration
	 */
	private void buildTable(FaceProducer builder, Table table,
			Element configuration) {
		
		Boolean reorderingAllowed = getBool(configuration.attribute("reorderingAllowed"));
		if (reorderingAllowed!= null) {
			table.setColumnReorderingAllowed(reorderingAllowed);
		}
		
		Element columnsNode = configuration.element("columns");
		if (columnsNode == null) {
			return;
		}
		List<Element> columns = columnsNode.elements("column");

		for (Element col : columns) {
			addColumnToTable(builder, table, configuration, col);
		}
	}

	/**
	 * @param builder
	 * @param table
	 * @param configuration
	 * @param col
	 */
	private void addColumnToTable(FaceProducer builder, Table table,
			Element configuration, Element col) {
		
		Attribute aId = col.attribute("id");
		if (aId == null) {
			logger.warn ("There is no property 'id' in column definition: "+col);
			return;
		}

		Attribute aColumnAlignment = col.attribute("alignment");
		Attribute aColumnIcon = col.attribute("icon");
		String columnHeader = col.attributeValue("header");
		Attribute aDefaultValue = col.attribute("defaultValue");
		Attribute aType = col.attribute("type");
		String caption = col.attributeValue("caption");
		if (columnHeader == null && caption != null) {
			columnHeader = caption;
		}

		String sType = aType == null ? "String" : aType.getValue();
		sType = clarifyTypeName(sType);

		Class type = getColumnType(sType);
		
		if (type==null) {
		     sType = null;
		}

		Object defaultValue = getDefaultValue(aDefaultValue, sType);

		Table.Align columnAlignment = getColumnAlignment(aColumnAlignment);

		Resource columnIcon = null;
		// TODO придумать механизм описания ресурса и вынести его на уровень
		// абстракции
		/**
		 * if (aColumnIcon != null) { columnIcon = new Resource() {
		 * 
		 * @Override public String getMIMEType() { return null; } }; }
		 */

		String id = aId.getValue();
		table.addContainerProperty(id, type, defaultValue, columnHeader, columnIcon, columnAlignment);

		
		setColumnCollapsed(id, table, col);
			
		setColumnCollapsible(id, table, col);
		
		setColumnExpandRatio(id, table, col);
		
		setColumnFooter(id, table, col);
		
		setColumnWidth(id, table, col);
	}

	/**
	 * @param sType
	 * @return
	 */
	private Class getColumnType(String sType) {
		Class type = null;
		try {
			type = Class.forName("java.lang." + sType);
		} catch (Exception e) {
			try {
				type = Class.forName("java.util." + sType);
			} catch (Exception e2) {
				try {
					type = Class.forName(sType);
				} catch (Exception e3) {
					// undefined type. skip
					logger.error("Invalid column type "+sType);
					
				}
			}
		}
		return type;
	}

	/**
	 * @param sType
	 * @return
	 */
	private String clarifyTypeName(String sType) {
		if (sType.toLowerCase().equals("int")
				|| sType.toLowerCase().equals("integer"))
			sType = "Integer";
		if (sType.toLowerCase().equals("float"))
			sType = "Float";
		if (sType.toLowerCase().equals("double"))
			sType = "Double";
		if (sType.toLowerCase().equals("date"))
			sType = "Date";
		return sType;
	}

	/**
	 * @param aDefaultValue
	 * @param sType
	 * @return
	 */
	private Object getDefaultValue(Attribute aDefaultValue, String sType) {
		Object defaultValue = null;
		if (aDefaultValue != null && sType != null) {
			String sDefaultValue = aDefaultValue.getValue();
			defaultValue = prepareDefaultValue(sType, sDefaultValue);
		}
		return defaultValue;
	}

	/**
	 * @param aColumnAlignment
	 * @return
	 */
	private Table.Align getColumnAlignment(Attribute aColumnAlignment) {
		if (aColumnAlignment != null) {
			try {
				return Table.Align.valueOf(aColumnAlignment.getValue());
			} catch (Exception e) {
				// skip
			}
		}
		return null;
	}

	/**
	 * @param id
	 * @param table
	 * @param col
	 */
	private void setColumnWidth(String id, Table table, Element col) {
		Attribute aWidth = col.attribute("width");
		Integer width = null;
		if (aWidth != null) {
			try {
				width = Integer.valueOf(aWidth.getValue());
			} catch (Exception e) {
				logger.warn("Invalid width value:"+aWidth.getValue());
			}
		}
		if (width != null) {
			table.setColumnWidth(id, width);
		}
	}

	/**
	 * @param id
	 * @param table
	 * @param col
	 */
	private void setColumnFooter(String id, Table table, Element col) {
		Attribute aFooter = col.attribute("footer");
		if (aFooter != null) {
			table.setColumnFooter(id, aFooter.getValue());
		}
	}

	/**
	 * @param id
	 * @param table
	 * @param col
	 */
	private void setColumnExpandRatio(String id, Table table, Element col) {
		Attribute aExpandRatio = col.attribute("expandRatio");
		Float expandRatio = null;
		if (aExpandRatio != null) {
			try {
				expandRatio = Float.valueOf(aExpandRatio.getValue());
			} catch (Exception e) {
				logger.error ("Could not convert to float '"+aExpandRatio.getValue()+"'");
			}
		}
		if (expandRatio != null) {
			table.setColumnExpandRatio(id, expandRatio);
		}
	}

	/**
	 * @param id
	 * @param table
	 * @param col
	 */
	private void setColumnCollapsible(String id, Table table, Element col) {
		Boolean collapsible = getBool(col.attribute("collapsible"));
		if (collapsible != null) {
			table.setColumnCollapsible(id, collapsible);
		}
	}

	/**
	 * @param id
	 * @param table
	 * @param col
	 */
	private void setColumnCollapsed(String id, Table table, Element col) {
		Boolean collapsed = getBool(col.attribute("collapsed"));
		if (collapsed != null) {
			table.setColumnCollapsed(id, collapsed);
		}
	}
	
	/**
	 * @param attr
	 * @return
	 */
	private Boolean getBool (Attribute attr) {
		if (attr == null) 
			return null;
		try {
			return Boolean.getBoolean(attr.getValue());
		} catch (Exception e) {
			// skip
		}
		return null;
	}

	/**
	 * @param type
	 * @param sDefaultValue
	 * @return
	 */
	private Object prepareDefaultValue(String type, String sDefaultValue) {
		Object defaultValue = null;
		if ("integer".equals(type.toLowerCase())
				|| "int".equals(type.toLowerCase())) {
			try {
				defaultValue = Integer.valueOf(sDefaultValue);
			} catch (Exception e) {
				// skip
			}
		} else if ("float".equals(type.toLowerCase())) {
			try {
				defaultValue = Float.valueOf(sDefaultValue);
			} catch (Exception e) {
				// skip
			}
		} else if ("double".equals(type.toLowerCase())) {
			try {
				defaultValue = Double.valueOf(sDefaultValue);
			} catch (Exception e) {
				// skip
			}
		} else if ("date".equals(type.toLowerCase())) {
			if (sDefaultValue.toLowerCase().equals("now")) {
				defaultValue = new Date();
			} else
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd");
					defaultValue = sdf.parse(sDefaultValue);
				} catch (Exception e) {
					// skip
				}
		}
		return defaultValue;
	}

}
