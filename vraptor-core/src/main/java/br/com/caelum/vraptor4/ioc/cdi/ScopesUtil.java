package br.com.caelum.vraptor4.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import br.com.caelum.vraptor4.ioc.ApplicationScoped;
import br.com.caelum.vraptor4.ioc.PrototypeScoped;
import br.com.caelum.vraptor4.ioc.SessionScoped;

/**
 * This class should be used for bean registration at startup time
 * 
 * @author Alberto Souza
 * 
 */
// TODO create unit tests
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ScopesUtil {

	private final List cdiScopes = Arrays.asList(
			javax.enterprise.context.ApplicationScoped.class,
			javax.enterprise.context.SessionScoped.class, Dependent.class,
			RequestScoped.class);

	private final List vraptorScopes = Arrays.asList(ApplicationScoped.class,
			SessionScoped.class, br.com.caelum.vraptor4.ioc.RequestScoped.class,
			PrototypeScoped.class);

	private final Map<Class<? extends Annotation>, Class<? extends Annotation>> cdiToVraptorScopes = 
			new HashMap<Class<? extends Annotation>, Class<? extends Annotation>>();
	
	{
		cdiToVraptorScopes.put(javax.enterprise.context.ApplicationScoped.class,ApplicationScoped.class);
		cdiToVraptorScopes.put(javax.enterprise.context.SessionScoped.class,SessionScoped.class);
		cdiToVraptorScopes.put(RequestScoped.class,br.com.caelum.vraptor4.ioc.RequestScoped.class);
		cdiToVraptorScopes.put(javax.enterprise.context.Dependent.class,PrototypeScoped.class);
	}

	private HashSet<Class<? extends Annotation>> findAnnotations(final Class<?> componentType,
			final List scopesAnnotation) {
		Annotation[] annotations = componentType.getAnnotations();
		HashSet<Class<? extends Annotation>> result = new HashSet<Class<? extends Annotation>>();
		for (Annotation componentAnnotation : annotations) {
			for (Class scopeAnnotation : (List<Class>) scopesAnnotation) {
				if (componentAnnotation.annotationType().equals(scopeAnnotation)) {
					result.add(componentAnnotation.annotationType());
				}
			}
		}
		return result;
	}

	private HashSet<Class<? extends Annotation>> cdiScopes(final Class<?> componentType) {
		return findAnnotations(componentType, cdiScopes);
	}

	public ScopeInfo isScoped(Class<?> clazz) {
		ScopeInfo scopedInfo = new ScopeInfo();
		Iterator iterator = cdiScopes(clazz).iterator();
		if(iterator.hasNext()){
			Class<? extends Annotation> cdiScope = (Class<? extends Annotation>) iterator.next();
			scopedInfo.setScope(cdiToVraptorScopes.get(cdiScope));
			return scopedInfo;
		}
		iterator = vraptorScopes(clazz).iterator();
		if(iterator.hasNext()){
			Class<? extends Annotation> vraptorScope = (Class<? extends Annotation>) iterator.next();
			scopedInfo.setScope(vraptorScope);			
		}
		return scopedInfo;
	}

	private HashSet<Class<? extends Annotation>> vraptorScopes(final Class<?> componentType) {
		return findAnnotations(componentType, vraptorScopes);
	}

}
