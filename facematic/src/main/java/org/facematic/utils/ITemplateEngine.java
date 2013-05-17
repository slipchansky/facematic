package org.facematic.utils;

import java.io.IOException;
import java.util.Map;

public interface ITemplateEngine {

	/**
	 * Поместить в контекст именованную переменную
	 * @param name
	 * @param value
	 * @return
	 */
	public abstract ITemplateEngine put(String name, Object value);

	/**
	 * Поместить в контекст именованные переменные из карты.
	 * @param m
	 * @return
	 */
	public abstract ITemplateEngine put(Map m);

	/**
	 * Выполняет трансляцию специфицированного темплейта в контексте биндинга экземпляра GroovyEngine
	 * @param code
	 * @return
	 * @throws CompilationFailedException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public abstract String translate(String code);

}