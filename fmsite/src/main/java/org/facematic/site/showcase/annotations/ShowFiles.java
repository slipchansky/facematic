package org.facematic.site.showcase.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ShowFiles {
	boolean java ();    // Must we show Java source code ?
	boolean xml ();     // Must we show XML for this face ?
	boolean sample() default true; // Must we show the result face ?
}
