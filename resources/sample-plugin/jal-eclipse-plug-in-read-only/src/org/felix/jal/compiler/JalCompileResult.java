package org.felix.jal.compiler;

/*
jal 2.4o (compiled May  8 2011)
generating p-code
1575 tokens, 119718 chars; 3041 lines; 8 files
generating PIC code pass 1
generating PIC code pass 2
writing result
Code area: 298 of 32768 used (bytes)
Data area: 21 of 1408 used
Software stack available: 1387 bytes
Hardware stack depth 2 of 31
0 errors, 0 warnings 	
 */

public class JalCompileResult {
	private String compiler;
	private int tokens;
	private int chars;
	private int lines;
	private int files;	
	private int codeAreaBytesUsed;
	private int codeAreaBytesTotal;
	private int dataAreaBytesUsed;
	private int dataAreaBytesTotal;
	private int softStackAvailable;
	private int hardStackDepthUsed;
	private int hardStackDepthTotal;
	private int numErrors;
	private int numWarnings;
	public int getNumErrors() {
		return numErrors;
	}
	public void setNumErrors(int numErrors) {
		this.numErrors = numErrors;
	}
	public int getNumWarnings() {
		return numWarnings;
	}
	public void setNumWarnings(int numWarnings) {
		this.numWarnings = numWarnings;
	}
	private String result;
	private JalErrors[] errors;

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public JalErrors[] getErrors() {
		return errors;
	}
	public void setErrors(JalErrors[] errors) {
		this.errors = errors;
	}
	
	public String getCompiler() {
		return compiler;
	}
	public void setCompiler(String compiler) {
		this.compiler = compiler;
	}
	public int getTokens() {
		return tokens;
	}
	public void setTokens(int tokens) {
		this.tokens = tokens;
	}
	public int getChars() {
		return chars;
	}
	public void setChars(int chars) {
		this.chars = chars;
	}
	public int getLines() {
		return lines;
	}
	public void setLines(int lines) {
		this.lines = lines;
	}
	public int getFiles() {
		return files;
	}
	public void setFiles(int files) {
		this.files = files;
	}
	public int getCodeAreaBytesUsed() {
		return codeAreaBytesUsed;
	}
	public void setCodeAreaBytesUsed(int codeAreaBytesUsed) {
		this.codeAreaBytesUsed = codeAreaBytesUsed;
	}
	public int getCodeAreaBytesTotal() {
		return codeAreaBytesTotal;
	}
	public void setCodeAreaBytesTotal(int codeAreaBytesTotal) {
		this.codeAreaBytesTotal = codeAreaBytesTotal;
	}
	public int getDataAreaBytesUsed() {
		return dataAreaBytesUsed;
	}
	public void setDataAreaBytesUsed(int dataAreaBytesUsed) {
		this.dataAreaBytesUsed = dataAreaBytesUsed;
	}
	public int getDataAreaBytesTotal() {
		return dataAreaBytesTotal;
	}
	public void setDataAreaBytesTotal(int dataAreaBytesTotal) {
		this.dataAreaBytesTotal = dataAreaBytesTotal;
	}
	public int getSoftStackAvailable() {
		return softStackAvailable;
	}
	public void setSoftStackAvailable(int softStackAvailable) {
		this.softStackAvailable = softStackAvailable;
	}
	public int getHardStackDepthUsed() {
		return hardStackDepthUsed;
	}
	public void setHardStackDepthUsed(int hardStackDepthUsed) {
		this.hardStackDepthUsed = hardStackDepthUsed;
	}
	public int getHardStackDepthTotal() {
		return hardStackDepthTotal;
	}
	public void setHardStackDepthTotal(int hardStackDepthTotal) {
		this.hardStackDepthTotal = hardStackDepthTotal;
	}

}
