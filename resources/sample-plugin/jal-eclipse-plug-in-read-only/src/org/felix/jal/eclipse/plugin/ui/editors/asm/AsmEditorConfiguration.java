package org.felix.jal.eclipse.plugin.ui.editors.asm;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class AsmEditorConfiguration extends TextSourceViewerConfiguration {
	
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler pr = new PresentationReconciler();
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(AsmEditorScanner.getInstance());
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;
	}

}
