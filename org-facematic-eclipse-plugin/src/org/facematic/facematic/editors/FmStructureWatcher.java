package org.facematic.facematic.editors;

import java.util.HashMap;
import java.util.Map;

public class FmStructureWatcher {
	StringBuilder importsTextBuilder;
	StringBuilder controllerTextBuilder;
	Map<String, String> importedClasses = new HashMap();

	public FmStructureWatcher(String controllerName) {
		importsTextBuilder = new StringBuilder(
				"import org.facematic.core.annotations.FmViewComponent;\nimport org.facematic.core.annotations.FmController;\nimport org.facematic.core.mvc.FmBaseController;\n");
		controllerTextBuilder = new StringBuilder("public class "
				+ controllerName + " implements FmBaseController {\n");
	}

	public void putView(String name, Object view) {
		 
		controllerTextBuilder.append("     @FmViewComponent(name=\"" + name
				+ "\")\n");
		controllerTextBuilder.append("     private "
				+ view.getClass().getSimpleName()
				+ (" " + name).replace('.', '_') + ";\n\n");
		if (importedClasses.get(view.getClass().getCanonicalName()) == null) {
			importsTextBuilder.append("import "
					+ view.getClass().getCanonicalName() + ";\n");
			importedClasses.put(view.getClass().getCanonicalName(), "");
		}
	}

	public void putController(String name, Object controller) {
		controllerTextBuilder.append("     @FmController(name=\"" + name
				+ "\")\n");
		controllerTextBuilder.append("     private "
				+ controller.getClass().getSimpleName() + " "
				+ name.replace('.', '_') + "Controller;\n\n");
		if (importedClasses.get(controller.getClass().getCanonicalName()) == null) {
			importsTextBuilder.append("import "
					+ controller.getClass().getCanonicalName() + ";\n");
			importedClasses.put(controller.getClass().getCanonicalName(), "");
		}
	}
	
	@Override
	public String toString() {
		return importsTextBuilder+"\n\n"+controllerTextBuilder;
	}
}