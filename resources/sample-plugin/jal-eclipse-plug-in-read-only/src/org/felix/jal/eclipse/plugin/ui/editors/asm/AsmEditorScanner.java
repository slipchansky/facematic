package org.felix.jal.eclipse.plugin.ui.editors.asm;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.felix.jal.eclipse.plugin.ui.editors.JalNumberRule;
import org.felix.jal.eclipse.plugin.ui.preferences.JalEditorPreferences;
import org.felix.jal.lang.JalConstant;

public class AsmEditorScanner extends RuleBasedScanner {
	private Color COLOR_COMMENT;
	private Color COLOR_OTHER;
	private Color COLOR_STRING;
	private Color COLOR_ASM;
	private Color COLOR_NUMBERS;

	private static AsmEditorScanner instance = null;
	
	private AsmEditorScanner() {
		initialize();
	}
	
	public static AsmEditorScanner getInstance() {
		if (instance==null)
			return new AsmEditorScanner();
		return instance;
	}
	
	/**
	 * Initialise le scanner.
	 */
	public void initialize() {
		Display d = PlatformUI.getWorkbench().getDisplay();
		
		if (!JalEditorPreferences.isDarkSyntaxHighlightingEnabled()) {
			COLOR_COMMENT = new Color(d, 0, 128, 0);
			COLOR_OTHER = new Color(d, 0, 0, 0);
			COLOR_STRING = new Color(d, 0, 0, 200);
			COLOR_ASM = new Color(d, 0xa0,0 , 0);
			COLOR_NUMBERS = new Color(d, 128, 0, 0);
		}
		else {		
			RGB nail_polish_pink = new RGB(0xff, 0x00, 0x64);
			RGB sky_blue = new RGB(0x00, 0x88, 0xff);
			RGB white = new RGB(0xff, 0xff, 0xff);
			RGB spring_green = new RGB(0x3a, 0xd9, 0x00);
			RGB faded_yellow = new RGB(0xff, 0xee, 0x80);

			COLOR_COMMENT = new Color(d, sky_blue);
			COLOR_OTHER = new Color(d, white);
			COLOR_STRING = new Color(d, spring_green);
			COLOR_ASM = new Color(d, faded_yellow);
			COLOR_NUMBERS = new Color(d, nail_polish_pink);
		}
		
		Token comment = new Token(new TextAttribute(COLOR_COMMENT, null, SWT.ITALIC));
		Token other = new Token(new TextAttribute(COLOR_OTHER, null, SWT.NORMAL));
		Token string = new Token(new TextAttribute(COLOR_STRING, null, SWT.NORMAL));
		Token asm = new Token(new TextAttribute(COLOR_ASM, null, SWT.BOLD));
		Token numbers = new Token(new TextAttribute(COLOR_NUMBERS, null, SWT.NORMAL));
		
		WordRule rule = new WordRule(new IWordDetector() {
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}
		}, other);
		
		JalNumberRule numRule = new JalNumberRule(numbers);
				
		for (int i = 0;i<JalConstant.ASM_KEYWORDS.length;i++) {
			rule.addWord(JalConstant.ASM_KEYWORDS[i], asm);
			rule.addWord(JalConstant.ASM_KEYWORDS[i].toUpperCase(), asm);
		}

		setRules(new IRule[] {
				rule,
				numRule,
				new SingleLineRule(";", null, comment, (char) 0, true),
				new SingleLineRule("\"","\"", string, (char) 0, true)
				
		});		
	}

}
