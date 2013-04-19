package com.slipchansky.fm.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Помечает поле для помещения в контекст<br />
 * возможно не понадобится...
 * 
 * @author stas
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface FaceContext {
	

}
