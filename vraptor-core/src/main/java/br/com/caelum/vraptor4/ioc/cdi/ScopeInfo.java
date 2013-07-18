package br.com.caelum.vraptor4.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.util.AnnotationLiteral;

import br.com.caelum.vraptor4.ioc.ApplicationScoped;
import br.com.caelum.vraptor4.ioc.PrototypeScoped;
import br.com.caelum.vraptor4.ioc.RequestScoped;
import br.com.caelum.vraptor4.ioc.SessionScoped;

//TODO unit tests
@SuppressWarnings("serial")
public class ScopeInfo {

	private Class<? extends Annotation> scope;
	private final Map<Class<? extends Annotation>, AnnotationLiteral<? extends Annotation>> scopesLiterals = new HashMap<Class<? extends Annotation>, AnnotationLiteral<? extends Annotation>>();

	{
		scopesLiterals.put(ApplicationScoped.class,new AnnotationLiteral<ApplicationScoped>(){});
		scopesLiterals.put(SessionScoped.class,new AnnotationLiteral<SessionScoped>(){});
		scopesLiterals.put(RequestScoped.class,new AnnotationLiteral<RequestScoped>(){});
		scopesLiterals.put(PrototypeScoped.class,new AnnotationLiteral<PrototypeScoped>(){});

	}

	public ScopeInfo(Class<? extends Annotation> scope) {
		super();
		this.scope = scope;
	}
	
	public ScopeInfo() {
	}

	public void setScope(Class<? extends Annotation> scope) {
		this.scope = scope;
	}

	public boolean hasScope() {
		return scope != null;
	}

	public Class<? extends Annotation> getScope() {
		return scope;
	}

	public AnnotationLiteral<? extends Annotation> getLiteral() {
		if(this.scope==null){
			throw new IllegalStateException("Should not get literal if scope is not defined");
		}
		return this.scopesLiterals.get(this.scope);
	}

}
