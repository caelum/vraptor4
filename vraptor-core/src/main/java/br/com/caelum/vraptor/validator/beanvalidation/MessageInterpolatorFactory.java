/**
 *
 */
package br.com.caelum.vraptor.validator.beanvalidation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link MessageInterpolator}.
 * 
 * @author Lucas Cavalcanti
 * @since 3.1.3
 *
 */
@ApplicationScoped
public class MessageInterpolatorFactory{

	private static final Logger logger = LoggerFactory.getLogger(MessageInterpolatorFactory.class);

	private final ValidatorFactory factory;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected MessageInterpolatorFactory() {
		this(null);
	}

	@Inject
	public MessageInterpolatorFactory(ValidatorFactory factory) {
		this.factory = factory;
	}
	
	@Produces
	@ApplicationScoped
	public MessageInterpolator getInstance() {
		logger.debug("Initializing Bean Validator MessageInterpolator");
		return factory.getMessageInterpolator();
	}
}
