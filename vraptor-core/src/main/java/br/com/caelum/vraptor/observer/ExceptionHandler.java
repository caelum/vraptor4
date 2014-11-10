package br.com.caelum.vraptor.observer;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor.validator.ValidationException;
import net.vidageek.mirror.exception.ReflectionProviderException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ExceptionHandler {
	private final static Logger log = getLogger(ExceptionHandler.class);

	public void handle(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			throw new InterceptionException(exception);
		}
		if (exception instanceof ReflectionProviderException) {
			throwIfNotValidationException(exception, exception.getCause());
		}
		throwIfNotValidationException(exception, exception);
	}

	private void throwIfNotValidationException(Throwable original, Throwable alternativeCause) {
		Throwable cause = original.getCause();

		if (original instanceof ValidationException || cause instanceof ValidationException) {
			// fine... already parsed
			log.trace("swallowing {}", cause);
		} else {
			throw new ApplicationLogicException(alternativeCause);
		}
	}
}
