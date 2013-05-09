package br.com.caelum.vraptor.ioc.cdi.extensions;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.inject.Inject;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.Test;

import br.com.caelum.vraptor4.Controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"rawtypes","unchecked"})
public class AddInjectToConstructorExtensionTest {

	@Test
	public void shouldAddInjectToConstructorWithArgs(){
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(WithArgsConstructor.class);
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType());
		AddInjectToConstructorExtension extension = new AddInjectToConstructorExtension(builder);
		extension.processAnnotatedType(pat);
		pat.setAnnotatedType(builder.create());
		AnnotatedConstructor<?> argsConstructor = withArgs(pat.getAnnotatedType().getConstructors());
		AnnotatedConstructor<?> withoutArgsConstructor = withoutArgs(pat.getAnnotatedType().getConstructors());
		assertTrue(argsConstructor.isAnnotationPresent(Inject.class));
		assertFalse(withoutArgsConstructor.isAnnotationPresent(Inject.class));
		
	}
	
	@Test
	public void shouldNotAddInjectToConstructorWithoutArgs(){
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(WithNonArgsConstructor.class);
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType());
		AddInjectToConstructorExtension extension = new AddInjectToConstructorExtension(builder);
		extension.processAnnotatedType(pat);
		pat.setAnnotatedType(builder.create());
		AnnotatedConstructor<?> withoutArgsConstructor = withoutArgs(pat.getAnnotatedType().getConstructors());
		assertFalse(withoutArgsConstructor.isAnnotationPresent(Inject.class));
	}

	
	private static class WithNonArgsConstructor{
		
	}
	
	@Controller
	public static class WithArgsConstructor {
		private String field;

		public WithArgsConstructor(String field) {
			this.field = field;
		}
		
		public WithArgsConstructor() {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AnnotatedConstructor<?> withArgs(Set constructors) {
		Iterator<AnnotatedConstructor> annotatedConstructors = ((Set<AnnotatedConstructor>)constructors).iterator();
		while(annotatedConstructors.hasNext()){
			AnnotatedConstructor annotatedConstructor = annotatedConstructors.next();
			if(annotatedConstructor.getParameters().size() > 0){
				return annotatedConstructor;
			}
		}
		throw new RuntimeException("You should test a Class with at least one non args constructor");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AnnotatedConstructor<?> withoutArgs(Set constructors) {
		Iterator<AnnotatedConstructor> annotatedConstructors = ((Set<AnnotatedConstructor>)constructors).iterator();
		while(annotatedConstructors.hasNext()){
			AnnotatedConstructor annotatedConstructor = annotatedConstructors.next();
			if(annotatedConstructor.getParameters().size() == 0){
				return annotatedConstructor;
			}
		}
		throw new RuntimeException("You should test a Class with at least one non args constructor");
	}
}
