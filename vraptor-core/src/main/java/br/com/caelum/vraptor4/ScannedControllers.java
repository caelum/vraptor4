package br.com.caelum.vraptor4;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Alternative;

@Alternative
public class ScannedControllers {

	private final List<Class<?>> classes = new LinkedList<Class<?>>();

	public void add(Class<?> c) {
		classes.add(c);
	}

	public List<Class<?>> getClasses() {
		return classes;
	}
}
