package org.facematic.site.showcase.annotations;

public @interface ShowCase {
	String caption ();
	String description ();
	Class [] moreClasses () default {};
};
