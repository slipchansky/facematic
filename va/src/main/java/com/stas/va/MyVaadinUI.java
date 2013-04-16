package com.stas.va;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dom4j.DocumentException;

import com.slipchansky.fm.factory.ComponentFactory;
import com.vaadin.data.Container;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        setContent(layout);
        
//        Button button = new Button("Click Me");
//        button.addClickListener(new Button.ClickListener() {
//            public void buttonClick(ClickEvent event) {
//                layout.addComponent(new Label("Thank you for clicking"));
//            }
//        });
//        layout.addComponent(button);
        /*
        TableFieldFactory tff = new TableFieldFactory () {

			@Override
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				
				return null;
			}
        	
        };
        */ 
        
        //table.setTableFieldFactory(fieldFactory)
//        table.addContainerProperty ("firstName", Object.class, null);
//        table.addContainerProperty ("lastName", Object.class, null);
//        
//        table.addItem(new Object [] {"Stas", "Lipchansky"},new Integer(1));
//        
//        
//        table.setSizeFull();
//        table.setVisibleColumns(new String [] {"firstName", "lastName"});
//        table.setColumnHeader("firstName", "First name");
//        table.setColumnHeader("lastName", "Last name");
//        
//        layout.addComponent(table);
        
    	
    	//Link l = new Link ();
    	//l.setCaption("");
    	//l.setResource(resource)
    	
    	

    	
    	TextField f;
    	
    	ComponentFactory bldr = new ComponentFactory (this);
    	Component content;
		try {
			content = bldr.buildFromResource("com.slipchansky.markup.testform");
			
			/*
			final Button b = bldr.get ("mamabutton");
			final Button ok = bldr.get ("okbutton");
			
			b.addClickListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					ok.setVisible(false);
				}
			});
			*/
			
			
			setContent(content);
			 
			
			
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
    	
    }

}
