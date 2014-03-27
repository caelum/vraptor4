package br.com.caelum.vraptor.environment;

import java.net.URL;

/**
 * An environment has a set of key/value properties to be used within your application
 * 
 * @author Alexandre Atoji
 * @author Guilherme Silveira
 */
public interface Environment {

	/**
	 * Returns the environment name
	 * 
	 */
	String getName();

	/**
	 * Checks if a key is present
	 * 
	 */
	boolean has(String key);

	/**
	 * Checks if a key is equals to true if it's not present will return false
	 */
	boolean supports(String feature);

	/**
	 * Returns a key
	 */
	String get(String string);

	/**
	 * Returns a key or a default value if the value isn't set
	 */
	String get(String string, String defaultValue);

	/**
	 * Sets a key in memory. This will *not* affect any configuration file.
	 */
	void set(String key, String value);

	/**
	 * @return an {@link Iterable} with all keys
	 */
	Iterable<String> getKeys();

	/**
	 * An alias to {@link Environment#supports(String)} with {@link EnvironmentType#PRODUCTION}.
	 */
	boolean isProduction();

	/**
	 * An alias to {@link Environment#supports(String)} with {@link EnvironmentType#DEVELOPMENT}.
	 */
	boolean isDevelopment();

	/**
	 * An alias to {@link Environment#supports(String)} with {@link EnvironmentType#TEST}.
	 */
	boolean isTest();

	/**
	 * Locates a resource according to your current environment.
	 */
	URL getResource(String name);
}