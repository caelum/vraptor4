package br.com.caelum.vraptor.http;

public class Parameter {
	
	private final String name;
	private final int index;
	private final Class<?> type;
	
	public Parameter(String name, int index, Class<?> type) {
		this.name = name;
		this.index = index;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Class<?> getType() {
		return type;
	}
}
