package com.slipchansky.fm.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Обеспечивает возможность создать экземпляр контролера для специфицированного view, или определить путь поиска разметки View <br>
 *  
 *  
 *  @FaceController(viewName="com.slipchansky.views.SpecificView")<br/>
 *  class MyController ... {<br/>
 *  ...<br/>
 *  }<br/>
 *  <br/>
 *  @FaceController(viewPath="com.slipchansky.views")<br/>
 *  class MyController ... {<br/>
 *  ...<br/>
 *  }<br/>
 *  
 * @author stas
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FaceController {
	String viewName () default "";
	String viewPath () default "";
}
