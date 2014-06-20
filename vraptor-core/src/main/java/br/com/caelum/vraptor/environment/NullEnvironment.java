package br.com.caelum.vraptor.environment;

import java.net.URL;

import javax.enterprise.inject.Vetoed;

/**
 * Environment implementation that doesn't' anything.
 * @author Otávio Scherer Garcia
 * @since 4.0.1
 */
@Vetoed
public class NullEnvironment implements Environment {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean has(String key) {
		return false;
	}

	@Override
	public boolean supports(String feature) {
		return false;
	}

	@Override
	public String get(String string) {
		return null;
	}

	@Override
	public String get(String string, String defaultValue) {
		return null;
	}

	@Override
	public void set(String key, String value) {
		
	}

	@Override
	public Iterable<String> getKeys() {
		return null;
	}

	@Override
	public boolean isProduction() {
		return false;
	}

	@Override
	public boolean isDevelopment() {
		return false;
	}

	@Override
	public boolean isTest() {
		return false;
	}

	@Override
	public URL getResource(String name) {
		return null;
	}
}
