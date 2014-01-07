package br.com.caelum.vraptor.environment;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * An Enum to represents usual {@link Environment} types 
 * 
 * @author Rodrigo Turini
 * @since 4.0
 */
public enum EnvironmentType {

	PRODUCTION, DEVELOPMENT, ACCEPTANCE, TEST;

	public String getName() {
		return name().toLowerCase();
	}

	public static EnvironmentType of(String name) {
		if (isNullOrEmpty(name)) return null;
		return valueOf(name.toUpperCase());
	}
}