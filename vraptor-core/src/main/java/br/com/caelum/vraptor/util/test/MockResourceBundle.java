package br.com.caelum.vraptor.util.test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Mocked resource bundle that only returns the own key. Can be useful if you need to test without load a
 * resource bundle.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
public class MockResourceBundle extends ResourceBundle {

	/**
	 * Returns nothing.
	 */
	@Override
	public Enumeration<String> getKeys() {
		return Collections.emptyEnumeration();
	}

	/**
	 * Return the same key as value.
	 */
	@Override
	protected Object handleGetObject(String key) {
		return key;
	}
}