package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.VRaptorException;

/**
 * An exception to wrap all exceptions from reflection providers.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
public class ReflectionProviderException extends VRaptorException {

	public ReflectionProviderException(Throwable e) {
		super(e);
	}
}
