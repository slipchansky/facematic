package org.facematic.site.showcase.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ShowCase {
	public final String VAADIN_COMMON = "Vaadin common components implementation";
	public final String FACEMATIC_BASICS = "Facematic basics";
	public final String SMALL_CONVEIENCE = "Small convenience";
	public final String EXAMPLES_OF_USE = "Еxamples of use";
	
	String caption ();      // What will we see in navigation tree
	String description ();  // Description. The text in navigation tree on mouseover 
	String part ();         // Partition of navigation tree (VAADIN_COMMON, FACEMATIC_BASICS ...)
	Class [] moreClasses () default {}; // What other classes must we show in CaseViewer except this class
};
