package org.felix.jal.eclipse.plugin.ui.editors;

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
import org.felix.jal.eclipse.plugin.ui.preferences.JalEditorPreferences;
import org.felix.jal.lang.JalConstant;

public class JalEditorScanner extends RuleBasedScanner {
	private Color COLOR_KEYWORD;
	private Color COLOR_KEYWORD2;
	private Color COLOR_KEYWORD3;
	private Color COLOR_TYPES;
	private Color COLOR_COMMENT;
	private Color COLOR_OTHER;
	private Color COLOR_STRING;
	private Color COLOR_ANNOTATION;
	private Color COLOR_ASM;
	private Color COLOR_BUILT_IN;
	private Color COLOR_NUMBERS;

	private static JalEditorScanner instance = null;
	
	private JalEditorScanner() {
		initialize();
	}
	
	public static JalEditorScanner getInstance() {
		if (instance==null)
			return new JalEditorScanner();
		return instance;
	}
	
	/**
	 * Initialise le scanner.
	 */
	public void initialize() {
		Display d = PlatformUI.getWorkbench().getDisplay();
		
		if (!JalEditorPreferences.isDarkSyntaxHighlightingEnabled()) {
			COLOR_KEYWORD = new Color(d, 0, 0, 128);
			COLOR_KEYWORD2 = new Color(d, 0, 0, 200);
			COLOR_KEYWORD3 = new Color(d, 255, 0, 0);
			COLOR_TYPES = new Color(d, 0, 0, 128);
			COLOR_COMMENT = new Color(d, 0, 128, 0);
			COLOR_OTHER = new Color(d, 0, 0, 0);
			COLOR_STRING = new Color(d, 0, 0, 200);
			COLOR_ANNOTATION = new Color(d, 0, 128, 0);
			COLOR_ASM = new Color(d, 0xa0,0 , 0);
			COLOR_BUILT_IN = new Color(d, 0x09, 0, 0);
			COLOR_NUMBERS = new Color(d, 128, 0, 0);
		}
		else {		
			RGB bright_orange = new RGB(0xff, 0x9d, 0x00);
			//RGB orange = new RGB(0xea, 0x8d, 0x40);
			RGB dark_orange = new RGB(0xaa, 0x77, 0x77);
			RGB nail_polish_pink = new RGB(0xff, 0x00, 0x64);
			RGB teal_blue = new RGB(0x80, 0xff, 0xbb);
			RGB sky_blue = new RGB(0x00, 0x88, 0xff);
			RGB white = new RGB(0xff, 0xff, 0xff);
			RGB spring_green = new RGB(0x3a, 0xd9, 0x00);
			RGB faded_yellow = new RGB(0xff, 0xee, 0x80);
			//RGB dark_yellow = new RGB(0xff, 0xee, 0x20);

			COLOR_KEYWORD = new Color(d, bright_orange);
			COLOR_KEYWORD2 = new Color(d, nail_polish_pink);
			COLOR_KEYWORD3 = new Color(d, faded_yellow);
			COLOR_TYPES = new Color(d, teal_blue);
			COLOR_COMMENT = new Color(d, sky_blue);
			COLOR_OTHER = new Color(d, white);
			COLOR_STRING = new Color(d, spring_green);
			COLOR_ANNOTATION = new Color(d, sky_blue);
			COLOR_ASM = new Color(d, faded_yellow);
			COLOR_BUILT_IN = new Color(d, dark_orange);			
			COLOR_NUMBERS = new Color(d, nail_polish_pink);
		}
		
		Token keyword = new Token(new TextAttribute(COLOR_KEYWORD, null, SWT.BOLD));
		Token keyword2 = new Token(new TextAttribute(COLOR_KEYWORD2, null, SWT.NORMAL));
		Token keyword3 = new Token(new TextAttribute(COLOR_KEYWORD3, null, SWT.BOLD));
		Token types = new Token(new TextAttribute(COLOR_TYPES, null, SWT.BOLD));
		Token comment = new Token(new TextAttribute(COLOR_COMMENT, null, SWT.ITALIC));
		Token other = new Token(new TextAttribute(COLOR_OTHER, null, SWT.NORMAL));
		Token string = new Token(new TextAttribute(COLOR_STRING, null, SWT.NORMAL));
		Token annotation = new Token(new TextAttribute(COLOR_ANNOTATION, null, SWT.ITALIC));
		Token asm = new Token(new TextAttribute(COLOR_ASM, null, SWT.BOLD));
		Token builtinfunc = new Token(new TextAttribute(COLOR_BUILT_IN, null, SWT.BOLD));
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
				
		for (int i = 0;i<JalConstant.TYPES.length;i++) {
			rule.addWord(JalConstant.TYPES[i], types);
			rule.addWord(JalConstant.TYPES[i].toUpperCase(), types);
			
		}
		for (int i = 0;i<JalConstant.ASM_KEYWORDS.length;i++) {
			rule.addWord(JalConstant.ASM_KEYWORDS[i], asm);
			rule.addWord(JalConstant.ASM_KEYWORDS[i].toUpperCase(), asm);
		}
		for (int i = 0;i<JalConstant.KEYWORDS.length;i++) {
			rule.addWord(JalConstant.KEYWORDS[i], keyword);
			rule.addWord(JalConstant.KEYWORDS[i].toUpperCase(), keyword);
		}
		for (int i = 0;i<JalConstant.KEYWORDS2.length;i++) {
			rule.addWord(JalConstant.KEYWORDS2[i], keyword2);
			rule.addWord(JalConstant.KEYWORDS2[i].toUpperCase(), keyword2);
		}
		for (int i = 0;i<JalConstant.KEYWORDS3.length;i++) {
			rule.addWord(JalConstant.KEYWORDS3[i], keyword3);
			rule.addWord(JalConstant.KEYWORDS3[i].toUpperCase(), keyword3);
		}		
		
		for (int i = 0;i<JalConstant.BUILT_IN_FUNCTIONS.length;i++) {
			rule.addWord(JalConstant.BUILT_IN_FUNCTIONS[i], builtinfunc);
			rule.addWord(JalConstant.BUILT_IN_FUNCTIONS[i].toUpperCase(), builtinfunc);
		}
		
		setRules(new IRule[] {
				rule,
				numRule,
				new SingleLineRule("--;@", null, annotation, (char) 0, true),
				new SingleLineRule("--", null, comment, (char) 0, true),
				new SingleLineRule(";@", null, annotation, (char) 0, true),
				new SingleLineRule(";", null, comment, (char) 0, true),
				new SingleLineRule("\"","\"", string, (char) 0, true)
				
		});		
	}
}