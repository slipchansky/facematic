package com.slipchansky.fm.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Обеспечивает мапинг переменных контекста на поля класса
 * @author stas
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FaceContextAccessor {
	String key () default "";
}
