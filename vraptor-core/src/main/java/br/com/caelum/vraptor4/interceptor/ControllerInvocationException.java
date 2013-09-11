package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor4.InterceptionException;

/**
 * When a controller throws an exception, we use this one to wrap it, so
 * we can unwrap after it leaves the interceptor stack
 *
 */
@SuppressWarnings("serial")
public class ControllerInvocationException extends InterceptionException {

	public ControllerInvocationException(String msg) {
		super(msg);
	}

	public ControllerInvocationException(String msg, Throwable e) {
		super(msg, e);
	}

	public ControllerInvocationException(Throwable e) {
		super(e);
	}
}
