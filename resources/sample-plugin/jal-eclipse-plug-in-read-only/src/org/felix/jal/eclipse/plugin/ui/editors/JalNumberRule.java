package org.felix.jal.eclipse.plugin.ui.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.Token;

public class JalNumberRule extends NumberRule {

	public JalNumberRule(IToken token) {
		super(token);
	}

	public IToken evaluate(ICharacterScanner scanner) {
		int c= scanner.read();
		if (Character.isDigit((char)c)) {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
				do {
					c= scanner.read();
					
					if (Character.isLetter((char)c)) {
						scanner.unread();
						return Token.WHITESPACE;
					}
				} while (Character.isJavaIdentifierPart((char) c));
				scanner.unread();
				return fToken;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
	
}
