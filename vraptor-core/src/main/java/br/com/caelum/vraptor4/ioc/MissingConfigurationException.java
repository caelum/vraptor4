package br.com.caelum.vraptor4.ioc;

public class MissingConfigurationException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public MissingConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingConfigurationException(String s) {
		super(s);
	}

	public MissingConfigurationException(Throwable cause) {
		super(cause);
	}

}
