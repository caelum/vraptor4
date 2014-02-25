package br.com.caelum.vraptor.environment;

import com.google.common.base.Objects;

/**
 * An class to represent usual {@link Environment} types 
 * 
 * @author Rodrigo Turini
 * @author Chico Sokol
 * @since 4.0
 */
public class EnvironmentType {
	
	public static final EnvironmentType PRODUCTION = new EnvironmentType("production");
	public static final EnvironmentType DEVELOPMENT = new EnvironmentType("development");
	public static final EnvironmentType TEST = new EnvironmentType("test");

	private String name;
	
	public EnvironmentType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(name);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnvironmentType other = (EnvironmentType) obj;
		return Objects.equal(name, other.name);
	}
	
}
