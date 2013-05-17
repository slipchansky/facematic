package org.felix.jal.eclipse.plugin.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ISelection;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;

public class ToggleCommentAction extends Action {
	private JalEditor editor;
	
	public ToggleCommentAction(JalEditor editor){
		assert editor != null;
	    this.editor = editor;
	    setId("org.felix.jal.eclipse.plugin.ui.editors.ToggleCommentAction");
	}

	public final void run()
    {
        IDocument doc = editor.getDocument();

        if (!editor.isEditable())
        	return;
        
        if (doc == null)
        	return;
        
		ISelection selection = editor.getSelectionProvider().getSelection();
		if (isSelectionCommented(selection)) {
			unCommentSelection(selection);
		}
		else {
			commentSelection(selection);
		}
    }
	
	private boolean isSelectionCommented(ISelection selection) {
		if (!(selection instanceof ITextSelection))
			return false;

		ITextSelection textSelection= (ITextSelection) selection;
		if (textSelection.getStartLine() < 0 || textSelection.getEndLine() < 0)
			return false;

		IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());

		String[] prefixes = new String[]{";", "--"};
		try {

			// Perform the check
			for (int i= textSelection.getStartLine(); i < textSelection.getEndLine()+1; i++) {
				IRegion line= document.getLineInformation(i);
				String text= document.get(line.getOffset(), line.getLength());
				int[] found = TextUtilities.indexOf(prefixes, text, 0);

				if (found[0] == -1)
					// found a line which is not commented
					return false;

				String s= document.get(line.getOffset(), found[0]);
				s= s.trim();
				if (s.length() != 0)
					// found a line which is not commented
					return false;
			}

			return true;

		} catch (BadLocationException x) {
			// should not happen
			JalConsole.write(x);
		}
		
		return false;
	}
	
	private void unCommentSelection(ISelection selection) {
		IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());

		ITextSelection textSelection= (ITextSelection) selection;
		
		int startLine = textSelection.getStartLine();
		int endLine = textSelection.getEndLine();
		
		String[] prefixes = new String[]{";", "--"};
		try {

			for (int i= startLine; i < endLine+1; i++) {
				IRegion line= document.getLineInformation(i);
				String text= document.get(line.getOffset(), line.getLength());
				int[] found = TextUtilities.indexOf(prefixes, text, 0);

				if (found[0] == -1) {
					// found a line which is not commented
					continue;
				}				

				String s= document.get(line.getOffset(), found[0]);
				s= s.trim();
				if (s.length() != 0) {
					// found a line which is not commented
					continue;
				}

				document.replace(line.getOffset() + found[0], text.trim().indexOf(";")==0 ? 1:2, "");
			}
			
			IRegion regionStart = document.getLineInformation(startLine);
			IRegion regionEnd = document.getLineInformation(endLine);
			int length = regionEnd.getOffset() - regionStart.getOffset() + regionEnd.getLength();
			
			textSelection = new TextSelection(regionStart.getOffset(), length);
			editor.getSelectionProvider().setSelection(textSelection);			
		} catch (BadLocationException x) {
			// should not happen
			JalConsole.write(x);
		}
	}

	private void commentSelection(ISelection selection) {
		IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());

		ITextSelection textSelection= (ITextSelection) selection;

		int startLine = textSelection.getStartLine();
		int endLine = textSelection.getEndLine();
		
		try {

			for (int i= startLine; i < endLine+1; i++) {
				IRegion line= document.getLineInformation(i);
					document.replace(line.getOffset(), 0, "--");
			}

			IRegion regionStart = document.getLineInformation(startLine);
			IRegion regionEnd = document.getLineInformation(endLine);
			int length = regionEnd.getOffset() - regionStart.getOffset() + regionEnd.getLength();
			
			textSelection = new TextSelection(regionStart.getOffset(), length);
			editor.getSelectionProvider().setSelection(textSelection);			
		} catch (BadLocationException x) {
			// should not happen
			JalConsole.write(x);
		}
		
	}

	
}
