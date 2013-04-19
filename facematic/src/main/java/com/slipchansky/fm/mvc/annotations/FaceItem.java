package com.slipchansky.fm.mvc.annotations;

import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
public @interface FaceItem {
	String path () default "";
}
