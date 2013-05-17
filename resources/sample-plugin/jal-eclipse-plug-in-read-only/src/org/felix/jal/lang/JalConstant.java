package org.felix.jal.lang;

public class JalConstant  {
	private JalConstant() {}
	
	public static final String [] TYPES = new String[] {
		"bit","byte","sbyte","word","sword","dword","sdword","volatile",
		"const","var"
	};
	
	public static final String [] KEYWORDS = new String[] {
		"while","for","loop","forever","using","block",
	    "if","elsif","else","then","case","of","end","at",
	    "procedure","function","alias", "is","return",
	    "interrupt","task","start"
	};
	
	public static final String [] KEYWORDS2 = new String[] {
		"input","output","all_output","all_input",
		"on","off","true","false","high","low",
	};
	public static final String [] KEYWORDS3 = new String[] {
		"asm","assembler","fuses","bank","page","local","pragma","keep","suspend","target","clock","include",
		"bootloader", "long_start", "loader18", "variable_reuse"
	};
	
	public static final String [] ASM_KEYWORDS = new String[] {
		"return","sleep","clrwdt","retfie","clrw","nop","addwf","andwf",
		"clrf","comf","decf","decfsz","incf","incfsz","iorwf","movf","movwf",
		"rlf", "rrf","subwf","swapf","xorwf","andlw","iorlw","xorlw","addlw",
		"sublw","bcf","bsf","btfsc","btfss","movlw","retlw","call","goto"
		,"movfw"
		,"rlcf", "subfwb", "tblrd", "movlb", "addwfc", "lfsr", "rrcf", "org"
	};	
	
	public static final String [] BUILT_IN_FUNCTIONS = new String[] {
		"_usec_delay",
	};
	
}
