package com.slipchansky.fm.producer;

import com.vaadin.ui.Component;

public interface StructureListener {
	
	void putView(Object name, Component view);
	void putController(String name, Object controller);

}
