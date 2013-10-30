package br.com.caelum.vraptor.interceptor;

/**
 * Used to execute any step inside the AspectStyle interceptor.
 * @author Alberto
 *
 */
public interface StepExecutor {

	boolean accept();

	void execute(Object interceptor);

}