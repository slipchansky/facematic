package org.facematic.site.showcasestudio;

public class ShowCaseCollection {
	
	private static Class [] showCases = {
		org.facematic.site.showcase.simple.SimpleComponentsExample.class,
		org.facematic.site.showcase.containers.ContainersExample.class,
		org.facematic.site.showcase.controller.ControllerExample.class,
		org.facematic.site.showcase.composite.CompositeExample.class,
		org.facematic.site.showcase.inheritance.InheritanceExample.class,
		org.facematic.site.showcase.complex.ComplexExample.class,
		
		org.facematic.site.showcase.combobox.ComboBoxExample.class,
		org.facematic.site.showcase.tree.TreeExample.class,
		org.facematic.site.showcase.table.TableExample.class,
		org.facematic.site.showcase.databinding.DataBindingExample.class,
		org.facematic.site.showcase.overlay.OverlayExample.class,
		
		org.facematic.site.showcase.viewer.ShowCaseViewer.class,
		org.facematic.site.showcasestudio.ShowCaseStudio.class
	}; 
	
	public static Class [] getCases () {
		return showCases;
	}
}
