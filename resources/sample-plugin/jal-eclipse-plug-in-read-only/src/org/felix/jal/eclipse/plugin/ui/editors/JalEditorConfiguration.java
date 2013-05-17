package org.felix.jal.eclipse.plugin.ui.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.felix.jal.eclipse.plugin.ui.editors.hyperlink.JalElementHyperlinkDetector;


/**
 */
public class JalEditorConfiguration extends TextSourceViewerConfiguration {
	private JalEditor edit;
	public JalEditorConfiguration(JalEditor edit) {
		this.edit = edit;
	}
	
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler pr = new PresentationReconciler();
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(JalEditorScanner.getInstance());
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;
	}
	
	 public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		
		 // Create content assistant
		   ContentAssistant assistant = new ContentAssistant();
		   
		   // Create content assistant processor
		   IContentAssistProcessor processor = new JalContentAssistProcessor(edit);
		   
		   assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		       
		   // Return the content assistant   
		   return assistant;
	}
	 
	 public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		return new IHyperlinkDetector[]{new JalElementHyperlinkDetector(edit)};
	} 

}