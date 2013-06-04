package org.facematic.site.showcase.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ShowCase {
	String caption ();
	String description ();
	Class [] moreClasses () default {};
};