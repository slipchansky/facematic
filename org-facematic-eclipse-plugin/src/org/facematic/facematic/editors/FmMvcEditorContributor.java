package org.facematic.facematic.editors;


import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.facematic.Activator;
import org.facematic.facematic.editors.parts.FmXmlEditor;
import org.facematic.facematic.editors.parts.HaveParent;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class FmMvcEditorContributor extends MultiPageEditorActionBarContributor {
	private IEditorPart activeEditorPart;
	private Action updateControllerAction;
	private Action foreceRefreshAction;
	private FmMvcEditor mvcEditor;
	/**
	 * Creates a multi-page contributor.
	 */
	public FmMvcEditorContributor() {
		super();
		createActions();
	}
	/**
	 * Returns the action registed with the given text editor.
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}
	/* (non-JavaDoc)
	 * Method declared in AbstractMultiPageEditorActionBarContributor.
	 */

	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part)
			return;

		activeEditorPart = part;

		if (activeEditorPart != null && (activeEditorPart instanceof HaveParent ) ) {
				mvcEditor = ((HaveParent)activeEditorPart).getParent();
		}
		
		


		IActionBars actionBars = getActionBars();
		if (actionBars != null) {

			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

			actionBars.setGlobalActionHandler(
				ActionFactory.DELETE.getId(),
				getAction(editor, ITextEditorActionConstants.DELETE));
			actionBars.setGlobalActionHandler(
				ActionFactory.UNDO.getId(),
				getAction(editor, ITextEditorActionConstants.UNDO));
			actionBars.setGlobalActionHandler(
				ActionFactory.REDO.getId(),
				getAction(editor, ITextEditorActionConstants.REDO));
			actionBars.setGlobalActionHandler(
				ActionFactory.CUT.getId(),
				getAction(editor, ITextEditorActionConstants.CUT));
			actionBars.setGlobalActionHandler(
				ActionFactory.COPY.getId(),
				getAction(editor, ITextEditorActionConstants.COPY));
			actionBars.setGlobalActionHandler(
				ActionFactory.PASTE.getId(),
				getAction(editor, ITextEditorActionConstants.PASTE));
			actionBars.setGlobalActionHandler(
				ActionFactory.SELECT_ALL.getId(),
				getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			actionBars.setGlobalActionHandler(
				ActionFactory.FIND.getId(),
				getAction(editor, ITextEditorActionConstants.FIND));
			actionBars.setGlobalActionHandler(
				IDEActionFactory.BOOKMARK.getId(),
				getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			actionBars.updateActionBars();
		}
	}
	private void createActions() {
		updateControllerAction = new Action() {
			public void run() {
				updateController ();
			}
		};
		updateControllerAction.setText("Update Controller source code");
		updateControllerAction.setToolTipText("Update Controller source code");
		updateControllerAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJS_TASK_TSK));
		
		foreceRefreshAction = new Action() {
			public void run() {
				forceRefresh ();
			}
		};
		
		
		ImageDescriptor img = Activator.getImageDescriptor("icons/arrow_circle_double.png");
		foreceRefreshAction.setText("Force refresh");
		foreceRefreshAction.setToolTipText("Force refresh. Implement Controller modifications.");
		foreceRefreshAction.setImageDescriptor(img);
		
		
		
	}
	
	protected void forceRefresh() {
		if (mvcEditor != null){
			mvcEditor.forceRefresh();
		}
	}
	
	protected void updateController() {
		if (mvcEditor != null){
			mvcEditor.updateControllerSourceCode ();
		}
	}
	public void contributeToMenu(IMenuManager manager) {
		IMenuManager menu = new MenuManager("Facematic");
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		menu.add(updateControllerAction);
		menu.add(foreceRefreshAction);
		
	}
	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		manager.add(updateControllerAction);
		manager.add(foreceRefreshAction);
	}
}
