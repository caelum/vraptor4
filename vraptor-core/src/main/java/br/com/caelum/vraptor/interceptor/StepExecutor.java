package br.com.caelum.vraptor.interceptor;

/**
 * Used to execute any step inside the AspectStyle interceptor.
 * @author Alberto
 *
 */
public interface StepExecutor<R> {

	boolean accept();

	R execute(Object interceptor);

}