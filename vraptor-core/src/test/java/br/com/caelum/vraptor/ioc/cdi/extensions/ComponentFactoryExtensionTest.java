package br.com.caelum.vraptor.ioc.cdi.extensions;

import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import br.com.caelum.vraptor.ioc.ComponentFactory;

@SuppressWarnings({"serial","rawtypes","unchecked"})
public class ComponentFactoryExtensionTest {

	@Test
	public void shouldAddRequestScopeAndDefaultToProducerMethod() throws InterruptedException{
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(NonScopedFactory.class);
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType());
		ComponentFactoryExtension extension = new ComponentFactoryExtension(builder);
		extension.addProducesToComponentFactory(pat);
		pat.setAnnotatedType(builder.create());
		AnnotatedMethod producer = getProducer(pat.getAnnotatedType());
		assertTrue(producer.isAnnotationPresent(Produces.class));
		assertTrue(producer.isAnnotationPresent(RequestScoped.class));
	}
	
	@Test
	public void shouldAddClassScopeAndDefaultToProducerMethod(){
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(ScopedFactory.class);
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(pat.getAnnotatedType());
		ComponentFactoryExtension extension = new ComponentFactoryExtension(builder);
		extension.addProducesToComponentFactory(pat);
		pat.setAnnotatedType(builder.create());
		AnnotatedMethod producer = getProducer(pat.getAnnotatedType());
		assertTrue(producer.isAnnotationPresent(Produces.class));
		assertTrue(producer.isAnnotationPresent(ApplicationScoped.class));
	}
	
	private AnnotatedMethod getProducer(AnnotatedType annotatedType){
		/*
		 * for some reason the AnnotatedType has 2 methods 
		 * getInstance with different return types.
		 */
		Set<AnnotatedMethod> methods = annotatedType.getMethods();
		for (AnnotatedMethod annotatedMethod : methods) {
			Method javaMethod = annotatedMethod.getJavaMember();
			if(javaMethod.getReturnType().equals(String.class) && javaMethod.getName().equals("getInstance")){
				return annotatedMethod;
			}
		}
		throw new RuntimeException("You should use a ComponentFactory");
	}

	private static class NonScopedFactory implements ComponentFactory<String>{
		public String getInstance() {
			return null;
		}		
	}
	
	@ApplicationScoped
	private static class ScopedFactory implements ComponentFactory<String>{
		public String getInstance() {
			return null;
		}		
	}
}
