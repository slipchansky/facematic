package org.felix.jal.eclipse.plugin.ui.editors;

import org.eclipse.swt.custom.StyledText;


/**
 * Structure auto complete
 * 
 * invoked by JALEditorKeyEvent.keyPressed(KeyEvent evt)
 */
public final class JalCompleteStructureProcessor {

	private JalCompleteStructureProcessor() {}
	
	public static void codeAssist(StyledText text) {
		int offset = text.getCaretOffset();
		StringBuffer buf = new StringBuffer();
		StringBuffer bufNL= new StringBuffer();
		int searchOffset = offset-2;
		
		// last word search
		while (true) {
			try {
				String c = text.getText(searchOffset,searchOffset);
				if (Character.isWhitespace(c.charAt(0))){
					break;
				} else {
					buf.append(c);
					searchOffset --;
				}
			} catch (Exception e) {
				break;
			}
		}
		// indent search
		searchOffset = offset-2;
		while (true) {
			try {
				String c = text.getText(searchOffset,searchOffset);
				if (c.charAt(0) == '\n'){
					break;
				} else {
					bufNL.append(c);
					searchOffset --;
				}
			} catch (Exception e) {
				break;
			}
		}
		
		String indent = "";
		String res = buf.reverse().toString();
		String resNL = bufNL.reverse().toString();
		if (resNL != null) {
			for (int i= 0;i<resNL.length();i++) {
				if (resNL.charAt(i)==' ' || resNL.charAt(i)=='\t') {
					indent += resNL.charAt(i);
				} else {
					break;
				}
			}
		}
		String nl = "\n"+indent;
		String [] found = new String[]{
			"if",
			"elsif",
			"procedure",
			"function",
			"forever",
			"block",
		};
		String [] append = new String[]{
			"() then"+nl+"end if\n",
			"() then"+nl,
			"() is"+nl+"end procedure\n",
			"() return byte is"+nl+"end function\n",
			"loop"+nl+"end loop\n",
			nl+"end block",
		};
		int[] curs = new int[]{
			1, // if (cursor_here) then
			1, // elsif (cursor_here) then
			0, // procedure cursor_here () is
			0, // function cursor_here () return byte is
			5, // forever loop\ncursor_here
			1, // block\ncursor_here
		};
		for (int i = 0;i<found.length;i++) {
			if (res.equals(found[i])) {
				text.insert(append[i]);
				text.setCaretOffset(offset+curs[i]);
			}
		}
	}
	
}
