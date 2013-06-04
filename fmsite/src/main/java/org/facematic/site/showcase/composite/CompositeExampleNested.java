package org.facematic.site.showcase.composite;

import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;


@FmView(name="org.facematic.site.showcase.composite.CompositeExampleNested")
@ShowFiles(java = true, xml = true)
public class CompositeExampleNested {

    @FmReaction("Button[caption='first'].onClick")
    public void onFirst () {
        Notification.show("CompositeExampleNested first clicked", Type.WARNING_MESSAGE); 
    }

    @FmReaction("Button[caption='second'].onClick")
    public void onSecond () {
        Notification.show("CompositeExampleNested second clicked", Type.WARNING_MESSAGE); 
    }
}

