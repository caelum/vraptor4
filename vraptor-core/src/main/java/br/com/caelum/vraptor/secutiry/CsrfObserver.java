package br.com.caelum.vraptor.secutiry;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.events.MethodReady;

/**
 * If enabled, ensures CSRF protection on {@link Post} methods.
 *
 * @author Rodrigo Turini
 * @since 4.2
 */
@Alternative
@Dependent
public class CsrfObserver { // must to be an interceptor?

    @Inject private Csrf csrf;

    public void validateToken(@Observes MethodReady event) {
		if (!needCsrfProtection(event)) return;
		
		// check if csrf header name and token are equals injected csrf
		// check if a form parameter with csrf name are set, and equals injected csrf
		
	}
    
    public boolean needCsrfProtection(MethodReady event) {
    		return event.getControllerMethod().containsAnnotation(Post.class);
    		// TODO check if a X-Csrf-Token header with value nocheck is present?
    		// TODO check anything else another impl does
	}
}