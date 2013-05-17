package org.felix.jal.eclipse.plugin.ui.editors.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.felix.jal.eclipse.plugin.ui.console.JalConsole;
import org.felix.jal.eclipse.plugin.ui.editors.JalEditor;


/**
 * Based on JavaElementHyperlinkDetector (which uses the hyperlink mechanism added at eclipse 3.3)
 *
 * @author Fabio
 */
public class JalElementHyperlinkDetector extends AbstractHyperlinkDetector {

	private JalEditor textEditor;
	public JalElementHyperlinkDetector(JalEditor textEditor) {
		super();
		this.textEditor = textEditor ;
	}
	
    /**
     * Will basically hyperlink any non keyword word (and let the PythonHyperlink work later on to open it if that's possible)
     */
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

    	//ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);

//        if (region == null || canShowMultipleHyperlinks || !(this.textEditor instanceof JALEditor)) {
//        	
//        	return null;
//        }

        //SLS: no le gustó el canShowMultipleHyperlinks
        if (region == null || !(this.textEditor instanceof JalEditor)) {
        	
        	return null;
        }
        
        JalEditor editor = (JalEditor) this.textEditor;

        int offset = region.getOffset();

        try {
            IDocument document = this.textEditor.getDocumentProvider().getDocument(this.textEditor.getEditorInput());
            
            //see if we can find a word there
            IRegion wordRegion = JalWordFinder.findWord(document, offset);
            if (wordRegion == null) {
                return null;
            }
            
            //return a hyperlink even without trying to find the definition (which may be costly)
            return new IHyperlink[] { new JalHyperlink(wordRegion, editor) };
        } catch (Exception e) {
            //PydevPlugin.log(e);
        	JalConsole.write(e.getMessage());
            return null;
        }

    }

}
