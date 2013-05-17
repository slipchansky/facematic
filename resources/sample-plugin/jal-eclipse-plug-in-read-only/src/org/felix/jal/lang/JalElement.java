package org.felix.jal.lang;

public class JalElement implements Comparable<JalElement>{

	private String filename="";
	private JalContainer parent;
	private int type;
	private String name;
	private String typedName;
	
	public JalElement(String name, String typedName) {
		this.typedName = typedName;
		this.name = name;
	}
	
	public JalContainer getParent() {
		return parent;
	}
	public void setParent(JalContainer parent) {
		this.parent = parent;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypedName() {
		return typedName;
	}
	public void setTypedName(String typedName) {
		this.typedName = typedName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public int compareTo(JalElement o) {
		return this.getTypedName().compareTo(o.getTypedName());
	}

}
