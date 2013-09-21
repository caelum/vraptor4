package br.com.caelum.vraptor.musicjungle.interceptor;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

/**
 * Open session in view interceptor, that open a Hibernate Session before a request, and 
 * closes at the end of request.
 */
@Intercepts
public class TransactionInterceptor {
	
	@Inject
	private Session session;
	
	@Inject
	private Validator validator;
	
	@AroundCall
    public void intercept(SimpleInterceptorStack stack) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
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
