package br.com.caelum.vraptor.http;

/**
 * Constains method parameter information like name, index and type. This class is
 * based on Java 8 Parameter, allowing vraptor to be closer to Java 8.
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
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
	
	@Override
	public String toString() {
		return String.format("Parameter={}, index={}, type={}", name, index, type.getName());
	}
}
