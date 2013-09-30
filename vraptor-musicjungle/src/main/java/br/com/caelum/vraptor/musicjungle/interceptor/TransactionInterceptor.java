package br.com.caelum.vraptor.musicjungle.interceptor;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

/**
 * Open EntityManager in view interceptor, that opens an EntityManager and a transaction before a request, and 
 * closes at the end of request.
 */
@Intercepts
public class TransactionInterceptor {
	
	@Inject
	private EntityManager entityManager;
	
	@Inject
	private Validator validator;
	
	@AroundCall
    public void intercept(SimpleInterceptorStack stack) {
		EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            
            stack.next();
            
            if (!validator.hasErrors() && transaction.isActive()) {
                transaction.commit();
            }
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

}
