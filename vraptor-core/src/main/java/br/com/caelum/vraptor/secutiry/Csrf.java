package br.com.caelum.vraptor.secutiry;

/**
 * Cross Site Request Forgery (CSRF) interface.
 *
 * @author Rodrigo Turini
 * @since 4.2
 */
public interface Csrf {

	static final String CSRF_HEADER = "X-CSRF-Token";

	/**
	 * @return CSRF token
	 */
	public abstract String getToken();

	/**
	 * @return CSRF name, {@link Csrf#CSRF_HEADER} by default.
	 */
	public abstract String getName();
}