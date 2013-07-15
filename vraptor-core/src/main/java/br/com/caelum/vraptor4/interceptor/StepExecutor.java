package br.com.caelum.vraptor4.interceptor;

/**
 * Used to execute any step inside the AspectStyle interceptor.
 * @author Alberto
 *
 */
public interface StepExecutor<R> {

	public abstract boolean accept(Class<?> interceptorClass);

	public abstract R execute(Object interceptor);

}