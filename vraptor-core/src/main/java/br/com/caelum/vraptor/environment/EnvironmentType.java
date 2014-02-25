package br.com.caelum.vraptor.environment;

/**
 * An class to represent usual {@link Environment} types 
 * 
 * @author Rodrigo Turini
 * @author Chico Sokol
 * @since 4.0
 */
public class EnvironmentType {

	private String name;
	
	public EnvironmentType(String name) {
		this.name = name;
	}

	public static EnvironmentType production() {
		return new EnvironmentType("production");
	}
	
	public static EnvironmentType acceptance() {
		return new EnvironmentType("acceptance");
	}
	
	public static EnvironmentType test() {
		return new EnvironmentType("test");
	}
	
	public static EnvironmentType development() {
		return new EnvironmentType("development");
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
