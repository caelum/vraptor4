package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.InterceptionException;


/**
 * When a controller or JSP throws an exception, we use this one to wrap it, so
 * we can unwrap after it leaves the interceptor stack
 *
 */
@SuppressWarnings("serial")
public class ApplicationLogicException extends InterceptionException {

	public ApplicationLogicException(String msg) {
		super(msg);
	}

	public ApplicationLogicException(String msg, Throwable e) {
		super(msg, e);
	}

	public ApplicationLogicException(Throwable e) {
		super(e);
	}
}
