package br.com.caelum.vraptor.reflection;

import java.lang.invoke.MethodHandle;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.VRaptorException;

/**
 * This exception is used to handle {@link MethodHandle} invokeExact failures.
 * @author Alberto Souza
 *
 */
@Vetoed
public class MethodExecutorException extends VRaptorException{

	public MethodExecutorException(Throwable e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8852406152147489684L;

}
