package org.felix.jal.eclipse.plugin.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.felix.jal.eclipse.plugin.JalPlugin;
import org.felix.jal.eclipse.plugin.task.JalTask;
import org.felix.jal.eclipse.plugin.ui.outline.JalOutline;
import org.felix.jal.eclipse.plugin.ui.preferences.JalEditorPreferences;
import org.felix.jal.lang.JalParser;

public class JalEditor extends TextEditor {
	private static final String CONTENTASSIST_PROPOSAL_ID = "jpicshell.JALEditor.ContentAssistProposal";
	private static final String TODOMARKER_ID = "org.felix.jal.todomarker";
	private static final String TASK_TODO = "TODO";
	private static final String TASK_FIXME = "FIXME";
	private static final String TASK_UNKNOW = "UNKNOWN";
	private Pattern pattern;
	private ArrayList<JalTask> tasks;
	private JalOutline outline;
	private StyledText textWidget;
	private IPropertyChangeListener propertyChangeListener;
	
	public JalEditor() {
		super();
		setSourceViewerConfiguration(new JalEditorConfiguration(this));
		outline = new JalOutline(this);
		
		propertyChangeListener = new IPropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				updateColors();
			}
		};
		
		JalPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
		
		this.pattern = Pattern.compile(
				"(;\\s*(TODO|FIXME):?)|(--\\s*(TODO|FIXME):?)", 2);
		
		this.setDocumentProvider(new FileDocumentProvider());
		
		this.getDocumentProvider().addElementStateListener(new IElementStateListener() {
			
			@Override
			public void elementMoved(Object originalElement, Object movedElement) {
			}
			
			@Override
			public void elementDirtyStateChanged(Object element, boolean isDirty) {
				if (!isDirty)
					processMarkers();				
			}
			
			@Override
			public void elementDeleted(Object element) {
			}
			
			@Override
			public void elementContentReplaced(Object element) {
			}
			
			@Override
			public void elementContentAboutToBeReplaced(Object element) {
			}
		});
		
	}
	
	private void processMarkers() {		
		try {
			FileEditorInput input = (FileEditorInput) this.getEditorInput();
			IFile file = input.getFile();
			IDocument document = this.getDocumentProvider().getDocument(input);
			file.deleteMarkers(TODOMARKER_ID, true, IResource.DEPTH_INFINITE);
			this.tasks = new ArrayList<JalTask>();
			for (Matcher matcher = this.pattern.matcher(document.get()); 
				matcher.find(); 
				handleMatch(document, matcher.group().trim(),	matcher.start(), matcher.end()));

			Iterator<JalTask> i = this.tasks.iterator();
			while (i.hasNext()) {
				JalTask t = i.next();
				IMarker marker = file.createMarker(TODOMARKER_ID);
				marker.setAttribute(IMarker.LINE_NUMBER, t.line);
				marker.setAttribute(IMarker.MESSAGE, t.message);
				marker.setAttribute(IMarker.PRIORITY, new Integer(t.priority));
				marker.setAttribute(IMarker.USER_EDITABLE, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleMatch(IDocument document, String group, int start, int end) throws Exception {
		String type;
		if (group.toUpperCase().indexOf(TASK_TODO) > 0) {
			type = TASK_TODO;
		} else {
			if (group.toUpperCase().indexOf(TASK_FIXME) > 0) {
				type = TASK_FIXME;
			} else {
				type = TASK_UNKNOW;
			}
		}
		IRegion region = document.getLineInformationOfOffset(end);

		String message = document.get(end, region.getLength() + region.getOffset() - end);

		if (!message.trim().isEmpty()) {
			JalTask task = new JalTask(message.trim(), 
					document.getLineOfOffset(start) + 1, (type == TASK_FIXME) ? 2 : 1);
			this.tasks.add(task);
		}
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		
		processMarkers();
		
		this.getDocumentProvider().getDocument(input).addDocumentListener(new IDocumentListener() {
			
			private long lastRun = 0;
			
			@Override
			public void documentChanged(DocumentEvent event) {
	               long i = System.currentTimeMillis();
                   if (i > lastRun + 1000) {
                       lastRun = i;
                       processMarkers();
                   }
			}
			
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				
			}
		});
	}

	public void dispose() {
		super.dispose();
		JalPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
	 */
	protected void createActions() {
		super.createActions();
		// This action will fire a CONTENTASSIST_PROPOSALS operation
		// when executed
		IAction action = new TextOperationAction(
				ResourceBundle.getBundle(JalEditor.class.getPackage().getName()
						+ ".messages"), "ContentAssistProposal", this,
				SourceViewer.CONTENTASSIST_PROPOSALS);
		action.setActionDefinitionId(CONTENTASSIST_PROPOSAL_ID);
		// Tell the editor about this new action
		setAction(CONTENTASSIST_PROPOSAL_ID, action);
		// Tell the editor to execute this action
		// when Ctrl+Spacebar is pressed
		setActionActivationCode(CONTENTASSIST_PROPOSAL_ID, ' ', -1, SWT.CTRL);

		IAction toggleCommandAction = new ToggleCommentAction(this);
		action.setActionDefinitionId("org.felix.jal.eclipse.ui.commands.ToggleComment");
		setAction("org.felix.jal.eclipse.plugin.ui.editors.ToggleCommentAction", toggleCommandAction);
	}

	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
        ISourceViewer viewer = super.createSourceViewer(parent, ruler, styles);
        textWidget = viewer.getTextWidget();
        textWidget.addKeyListener(new JalEditorKeyEvent(this));
        return viewer;
	}

	public IDocument getDocument() {
		IDocumentProvider documentProvider = getDocumentProvider();
		if (documentProvider != null) {
			return documentProvider.getDocument(getEditorInput());
		}
		return null;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			return outline;
		}
		return super.getAdapter(adapter);
	}
	
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		try		{
			// Rebuild outline
			String content = getDocument().get();
			TreeViewer tree = outline.getViewer(); 
			tree.setInput(JalParser.getFileStructure(content));
			tree.expandAll();
		}
		catch (Exception ex){}
	}	

	public JalOutline getOutline() {
		return outline;
	}

	public void setOutline(JalOutline outline) {
		this.outline = outline;
	}

	public StyledText getTextWidget() {
		return textWidget;
	}

	public void setTextWidget(StyledText textWidget) {
		this.textWidget = textWidget;
	}
	
	@Override
	protected void initializeViewerColors(ISourceViewer viewer) {		
		super.initializeViewerColors(viewer);
		updateColors();
	}

	private void updateColors() {
		if (this.getSourceViewer() != null) {
			try {
			IPreferenceStore uiStore = EditorsUI.getPreferenceStore();
			if (JalEditorPreferences.isDarkSyntaxHighlightingEnabled()) {
				uiStore.setValue(PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
				uiStore.setValue(PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);
				PreferenceConverter.setValue(uiStore, PREFERENCE_COLOR_BACKGROUND, new RGB(0x00, 0x1b, 0x33));
				PreferenceConverter.setValue(uiStore, PREFERENCE_COLOR_FOREGROUND, new RGB(0xff, 0xff, 0xff));
				uiStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, true);
				PreferenceConverter.setValue(uiStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, new RGB(0x00, 0x3b, 0x70));
			} else {
				uiStore.setValue(PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);
				uiStore.setValue(PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, true);
				PreferenceConverter.setValue(uiStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, new RGB(0xE8, 0xF2, 0xFE));
			}
			JalEditorScanner.getInstance().initialize();
			ISourceViewerExtension2 sourceViewer = (ISourceViewerExtension2)this.getSourceViewer();
			if (sourceViewer != null)
				sourceViewer.unconfigure();
			this.getSourceViewer().configure(new JalEditorConfiguration(this));
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
