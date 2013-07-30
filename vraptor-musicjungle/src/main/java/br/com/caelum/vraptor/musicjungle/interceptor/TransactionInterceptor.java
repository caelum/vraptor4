package br.com.caelum.vraptor.musicjungle.interceptor;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Validator;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;

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
            if (!validator.hasErrors()) {
                transaction.commit();
            }
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

}
