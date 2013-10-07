package br.com.caelum.vraptor.cache;

public class CacheException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CacheException(String message, Exception cause) {
		super(message, cause);
	}
}
