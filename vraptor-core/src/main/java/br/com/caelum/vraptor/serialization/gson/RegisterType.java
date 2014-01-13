package br.com.caelum.vraptor.serialization.gson;

/**
 * Defines strategies for registering custom serialization or deserialization in
 * Gson.
 */
public enum RegisterType {
	/**
	 * Configures Gson for custom serialization or deserialization for a single
	 * type hierarchy.
	 */
	SINGLE,
	/**
	 * Configures Gson for custom serialization or deserialization for an
	 * inheritance type hierarchy
	 */
	INHERITANCE;
}
