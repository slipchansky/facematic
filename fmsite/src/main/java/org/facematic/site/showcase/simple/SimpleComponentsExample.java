package org.facematic.site.showcase.simple;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


import org.facematic.core.annotations.FmView;
import org.facematic.site.showcase.annotations.ShowCase;
import org.facematic.site.showcase.annotations.ShowFiles;

import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload;

@FmView(name = "org.facematic.site.showcase.simple.SimpleComponentsExample")
@ShowCase(part = ShowCase.VAADIN_COMMON, caption = "Vaadin simple elements", description = "Labels, Buttons, ... etc.")
@ShowFiles(java = false, xml = true)
public class SimpleComponentsExample {

}
