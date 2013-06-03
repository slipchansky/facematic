package org.facematic.site.showcase.tree;

import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.annotations.FmUI;
import org.facematic.core.annotations.FmView;
import org.facematic.core.annotations.FmReaction;
import org.facematic.core.annotations.FmViewComponent;
import org.facematic.core.mvc.FmBaseController;
import org.facematic.core.producer.FaceProducer;
import org.facematic.core.ui.FacematicUI;
import org.facematic.site.showcase.annotations.ShowCase;

import com.vaadin.ui.Component;


@FmView(name="org.facematic.site.showcase.tree.TreeExample")
@ShowCase (caption = "Tree example", description = "Vaadin Tree implementation")
public class TreeExample {
}

