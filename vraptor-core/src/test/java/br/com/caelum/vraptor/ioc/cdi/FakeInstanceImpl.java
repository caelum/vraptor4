package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;

/**
 * Fake implementation for {@link Instance} to test injection with list of elements.
 * @author Otávio Scherer Garcia
 */
public class FakeInstanceImpl<T> implements Instance<T> {
	
	private List<T> elements;

	public FakeInstanceImpl(List<T> elements) {
		this.elements = elements;
	}

	public FakeInstanceImpl(Object... elements) {
		this.elements = new ArrayList<>();
		for(Object element: elements)
			this.elements.add((T) element);
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

	@Override
	public T get() {
		return null;
	}

	@Override
	public Instance<T> select(Annotation... qualifiers) {
		return null;
	}

	@Override
	public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
		return null;
	}

	@Override
	public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
		return null;
	}

	@Override
	public boolean isUnsatisfied() {
		return false;
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public void destroy(T instance) {
		
	}
}
