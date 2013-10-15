package br.com.caelum.vraptor.musicjungle.interceptor;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.validator.Validator;

/**
 * Open EntityManager in view interceptor, that opens an EntityManager and a
 * transaction before a request, and closes at the end of request.
 */
@Intercepts
public class TransactionInterceptor {

	@Inject
	private EntityManager entityManager;

	@Inject
	private Validator validator;

	@Inject
	private MutableResponse response;

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {

		addRedirectListener();

		EntityTransaction transaction = null;
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();

			stack.next();

			commit(transaction);
			
		} finally {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	private void commit(EntityTransaction transaction) {
		if (!validator.hasErrors() && transaction.isActive()) {
			transaction.commit();
		}
	}

	/**
	 * We force the commit before the redirect, this way we can abort the
	 * redirect if a database error occurs.
	 */
	private void addRedirectListener() {
		response.addRedirectListener(new MutableResponse.RedirectListener() {
			@Override
			public void beforeRedirect() {
				commit(entityManager.getTransaction());
			}
		});
	}

}
