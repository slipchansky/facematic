package org.felix.jal.eclipse.plugin.ui.outline;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;
import org.felix.jal.eclipse.plugin.ui.editors.JalEditor;
import org.felix.jal.lang.JalContainer;
import org.felix.jal.lang.JalElement;
import org.felix.jal.lang.JalParser;


public class JalOutline extends ContentOutlinePage {
	private JalEditor editor;

	public JalOutline(JalEditor editor) {
		this.editor = editor;

	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer treeViewer = getTreeViewer();
		treeViewer.setContentProvider(new JalContentProvider());
		treeViewer.setLabelProvider(new JalLabelProvider());
		String content = editor.getDocument().get();
		treeViewer.setInput(JalParser.getFileStructure(content));
		treeViewer.expandAll();

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				if (event.getSelection().isEmpty()) {
					return;
				}

				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					Object elem = selection.getFirstElement();

					if (!(elem instanceof JalContainer)) {

						JalElement el = (JalElement) elem;

						String name = el.getName();
						FileUtil.selectText(editor, name);
					}
				}
			}
		});

	}

	public TreeViewer getViewer() {
		return getTreeViewer();
	}

	public JalEditor getEditor() {
		return editor;
	}

	public void setEditor(JalEditor editor) {
		this.editor = editor;
	}

}
