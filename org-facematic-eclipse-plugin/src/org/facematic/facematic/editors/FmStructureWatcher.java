package org.facematic.facematic.editors;

import java.util.HashMap;
import java.util.Map;

import org.facematic.plugin.utils.JavaSourceCodeTools;

public class FmStructureWatcher {
	Map<String, String> importedClasses = new HashMap();
	String sourceCode = null;
	private JavaSourceCodeTools sourceCodeHandler;
	

	public void putView(String name, Object view) {
		sourceCodeHandler.addImport(view.getClass().getCanonicalName());
		sourceCodeHandler.addField(name, view.getClass().getSimpleName(), "@FmViewComponent(name=\"" + name+ "\")");
	}


	public void putController(String name, Object controller) {
		sourceCodeHandler.addImport(controller.getClass().getCanonicalName());
		sourceCodeHandler.addImport("javax.inject.Inject");
		sourceCodeHandler.addField(name, controller.getClass().getSimpleName(), "@Inject");
		
	}
	
	public void setListener(String name, Class parameterType, Class producerClass, String producerName, String producerCaption, String eventName) {
		sourceCodeHandler.addImport (parameterType.getCanonicalName());
		sourceCodeHandler.setListener(name, parameterType, "event", producerClass, producerName, producerCaption, eventName);
	}
	
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
		sourceCodeHandler = JavaSourceCodeTools.getHandler(sourceCode);
	}

	public String getModifiedSourceCode() {
		return sourceCodeHandler.toString();
	}
}
