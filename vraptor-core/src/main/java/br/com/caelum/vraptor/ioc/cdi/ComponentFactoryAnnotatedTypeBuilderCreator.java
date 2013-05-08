package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

@SuppressWarnings({"serial","rawtypes","unchecked"})
public class ComponentFactoryAnnotatedTypeBuilderCreator {
	
	private AnnotatedTypeBuilder builder;
	
	public ComponentFactoryAnnotatedTypeBuilderCreator(AnnotatedTypeBuilder builder) {
		this.builder = builder;
	}
	
	public ComponentFactoryAnnotatedTypeBuilderCreator(){
		this(new AnnotatedTypeBuilder());
	}

	private final Map<Class<? extends Annotation>, AnnotationLiteral<? extends Annotation>> vraptorToCDIScopes = 
			new HashMap<Class<? extends Annotation>, AnnotationLiteral<? extends Annotation>>();
	
	{
		vraptorToCDIScopes.put(ApplicationScoped.class,new AnnotationLiteral<javax.enterprise.context.ApplicationScoped>(){});
		vraptorToCDIScopes.put(SessionScoped.class,new AnnotationLiteral<javax.enterprise.context.SessionScoped>(){});
		vraptorToCDIScopes.put(RequestScoped.class,new AnnotationLiteral<javax.enterprise.context.RequestScoped>(){});
		vraptorToCDIScopes.put(PrototypeScoped.class,new AnnotationLiteral<javax.enterprise.context.Dependent>(){});
	}	
	
	public AnnotatedTypeBuilder create(Class klass){
		builder.readFromType(klass);
		ScopeInfo scopedInfo = new ScopesUtil().isScoped(klass);
		if(!scopedInfo.hasScope()){
			scopedInfo.setScope(RequestScoped.class);
		}
		return create(klass,scopedInfo);
	}
	
	@SuppressWarnings({"unchecked"})
	private AnnotatedTypeBuilder create(Class klass,ScopeInfo scopeInfo){
		try{
			Method getInstance = klass.getMethod("getInstance");
			builder.addToMethod(getInstance,new AnnotationLiteral<Produces>() {});
			builder.addToMethod(getInstance,vraptorToCDIScopes.get(scopeInfo.getScope()));			
			return builder;
		}
		catch(Exception exception){
			throw new VRaptorException(exception);
		}
	}
}
