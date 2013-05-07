package br.com.caelum.vraptor.ioc.cdi.extensions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

@SuppressWarnings({"serial","rawtypes"})
public class ComponentExtensionTest {

	@Test
	public void shouldAddRequestAndDefaultForComponents() {
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(MyRequestComponent.class);
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType());
		ComponentExtension extension = new ComponentExtension(builder);
		extension.processAnnotatedType(pat);
		pat.setAnnotatedType(builder.create());
		assertTrue(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<RequestScoped>() {}));
	}
	
	@Test
	public void shouldNotAddRequestIfAnotherScopeWasUsed() {
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(MySessionComponent.class);
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType());
		ComponentExtension extension = new ComponentExtension(builder);
		extension.processAnnotatedType(pat);
		pat.setAnnotatedType(builder.create());
		assertFalse(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<RequestScoped>() {}));
		assertTrue(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<SessionScoped>() {}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldIgnoreNonComponents() {
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(SimpleClass.class);
		AnnotatedType annotatedTypeBeforeExtension = pat.getAnnotatedType();
		AnnotatedTypeBuilder builder = spy(new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType()));
		ComponentExtension extension = new ComponentExtension(builder);
		extension.processAnnotatedType(pat);
		pat.setAnnotatedType(builder.create());
		verify(builder,never()).addToClass(org.mockito.Matchers.any(AnnotationLiteral.class));
	}

	@Component
	private static class MyRequestComponent {

	}
	
	@Component
	@SessionScoped
	private static class MySessionComponent {
		
	}
	
	private static class SimpleClass {
		
	}
}
