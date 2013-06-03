package org.facematic.site.showcase.combobox;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.site.showcase.annotations.ShowCase;

import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Notification;


@ShowCase (caption = "Enum ComboBox", description = "Shows simple way for binding Enum to ComboBox", moreClasses = { EnumForCombo.class})
@FmView(name="org.facematic.site.showcase.combobox.ComboBoxExample")
public class ComboBoxExample {

	private static final Logger logger = LoggerFactory.getLogger(ComboBoxExample.class);
	
    @FmReaction("ComboBox#comboBox.onChange")
    public void comboChanged (ValueChangeEvent event) {
         Notification.show (""+event.getProperty().getValue()); 
    }
    
}

