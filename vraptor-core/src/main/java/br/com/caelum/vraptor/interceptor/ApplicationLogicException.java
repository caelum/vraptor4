package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;


/**
 * When a controller or JSP throws an exception, we use this one to wrap it, so
 * we can unwrap after it leaves the interceptor stack
 *
 */
public class ApplicationLogicException extends InterceptionException {

	private static final long serialVersionUID = -8388907262726903974L;

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
