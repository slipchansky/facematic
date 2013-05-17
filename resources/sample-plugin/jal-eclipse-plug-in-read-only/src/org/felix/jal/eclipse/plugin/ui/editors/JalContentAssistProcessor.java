/*******************************************************************************
 * Keyword Auto complete [ctrl+space]
 *******************************************************************************/
package org.felix.jal.eclipse.plugin.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.custom.StyledText;

/**
 * 
 */
public class JalContentAssistProcessor implements IContentAssistProcessor {
	private JalEditor editor = null;
	public JalContentAssistProcessor(JalEditor editor) {
		this.editor = editor;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(
		ITextViewer viewer,
		int documentOffset) {
		// Retrieve current document
		IDocument doc = viewer.getDocument();
		// Retrieve current selection range
		//Point selectedRange = viewer.getSelectedRange();
		List<ICompletionProposal> propList = new ArrayList<ICompletionProposal>();
		// Compile keyword list for proposals
		
		// Retrieve selected text
		// String text = doc.get(selectedRange.x, selectedRange.y);
		// Compute completion proposals
		// Retrieve qualifier
		String qualifier = getQualifier(doc, documentOffset);
		// Compute completion proposals
		computeProposals(qualifier, documentOffset, propList);
		 
		// Create completion proposal array
		ICompletionProposal[] proposals =
			new ICompletionProposal[propList.size()];
		// and fill with list elements
		propList.toArray(proposals);
		// Return the proposals
		return proposals;
	}
	
	
	private String getQualifier(IDocument doc, int documentOffset) {
		// Use string buffer to collect characters
		StringBuffer buf = new StringBuffer();
		while (true) {
			try {
				// Read character backwards
				char c = doc.getChar(--documentOffset);
				// This was not the start of a tag
				if (Character.isWhitespace(c)) {
					return buf.reverse().toString();
				} else {
					// Collect character
					buf.append(c);
				}
			} catch (BadLocationException e) {
				// Document start reached, no tag found
				return buf.reverse().toString();
			}
		}
	}
	
		
	

	/**
	 * @param qualifier - partially entered HTML tag
	 * @param documentOffset - current cursor position
	 * @param propList - result list
	 */
	private void computeProposals(
		String qualifier,
		int documentOffset,
		List<ICompletionProposal> propList
		) {
		int qlen = qualifier.length();
		
		String[] words = _getWords(qualifier);
			
		// Loop through all proposals
		for (int i = 0; i < words.length; i++) {
			String startTag = words[i];
			// Check if proposal matches qualifier
			if (startTag.toUpperCase().startsWith(qualifier.toUpperCase())) {
				// Yes � compute whole proposal text
				String text = startTag;
				// Derive cursor position
				int cursor = startTag.length();
				// Construct proposal
				CompletionProposal proposal =
					new CompletionProposal(
						text,
						documentOffset - qlen,
						qlen,
						cursor);
				// and add to result list
				propList.add(proposal);
			}
		}
	}
	
	

	private String[] _getWords(String qualifier) {
		String textToCurs =  getLineTextToCursor();
		try {
			return JalAutoCompleteHelper.getWords(textToCurs,editor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String[]{};
	}

	private String getLineTextToCursor () {
		
		StyledText text = editor.getTextWidget();
		int offset = text.getCaretOffset();
		StringBuffer buf = new StringBuffer();
		int searchOffset = offset-2;
		while (true) {
			try {
				String c = text.getText(searchOffset,searchOffset);
				if (c.charAt(0)=='\n'){
					break;
				} else {
					buf.append(c);
					searchOffset --;
				}
			} catch (Exception e) {
				break;
			}
		}
		return buf.reverse().toString();
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(
		ITextViewer viewer,
		int documentOffset) {
		// Retrieve selected range
        // Point selectedRange = viewer.getSelectedRange();
		return new ContextInformation[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		// Make proposals automatically after the following characters
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return new ContextInformationValidator(this);
	}

}

