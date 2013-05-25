package org.facematic.util.data.managedcontainer;

import java.util.HashMap;
import java.util.Map;

import org.facematic.util.data.managedcontainer.Action;
import org.facematic.util.data.managedcontainer.ManagedContainer;
import org.facematic.util.data.managedcontainer.ManagedContainerItem;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author "Stanislav Lipchansky"
 *
 */
public class RowControls extends HorizontalLayout {
	private static final Resource UP_ICON = new ThemeResource("../runo/icons/16/arrow-up.png");
	private static final Resource DOWN_ICON = new ThemeResource("../runo/icons/16/arrow-down.png");
	private static final Resource DELETE_ICON = new ThemeResource("../runo/icons/16/cancel.png");
	private static final Resource EDIT_ICON = new ThemeResource("../runo/icons/16/document-txt.png");
	
		private ManagedContainerItem row;
		private Button up;
		private Button down;
		private Button delete;
		private HorizontalLayout control;
		private Map<Action, Button> buttons = new HashMap<Action, Button> ();
		private ManagedContainer container;

		public RowControls (ManagedContainerItem row, ManagedContainer container) {
			this.row= row;
			this.container = container;
			addComponent(makeButton(Action.EDIT, EDIT_ICON));
			addComponent(makeButton(Action.UP, UP_ICON));
			addComponent(makeButton(Action.DOWN, DOWN_ICON));
			addComponent(makeButton(Action.DELETE, DELETE_ICON));
		}

		private Button makeButton(final Action action, Resource icon) {
			Button button = new Button();
			button.setIcon(icon);
			button.setDescription(action.getDescription());
			button.setStyleName(BaseTheme.BUTTON_LINK);
			
			button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					switch (action) {
					case EDIT:
						edit();
						break;
					case UP:
						moveUp();
						break;
					case DOWN:
						moveDown();
						break;
					case DELETE:
						remove();
						break;
					}
				}

			});
			buttons.put(action, button);
			return button;
		}
		
		public void hideAction (Action a ) {
			buttons.get(a).setVisible(false);
		}
		
		public void showAction (Action a) {
			buttons.get(a).setVisible(true);
		}

		private void moveDown() {
			container.moveDown (row);
		}

		private void edit() {
			container.edit (row);
		}
		
		protected void remove() {
			container.remove (row);
		}

		protected void moveUp() {
			container.moveUp (row);
		}

  }
