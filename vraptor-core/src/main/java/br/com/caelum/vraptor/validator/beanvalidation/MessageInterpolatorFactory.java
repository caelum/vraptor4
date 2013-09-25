/**
 *
 */
package br.com.caelum.vraptor.validator.beanvalidation;

import javax.annotation.PostConstruct;
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

	private ValidatorFactory factory;
	private MessageInterpolator interpolator;
	
	//CDI eyes only
	@Deprecated
	public MessageInterpolatorFactory() {
	}

	@Inject
	public MessageInterpolatorFactory(ValidatorFactory factory) {
		this.factory = factory;
	}
	
	@PostConstruct
	public void createInterpolator() {
		interpolator = factory.getMessageInterpolator();
		logger.debug("Initializing Bean Validator MessageInterpolator");
	}

	@Produces @javax.enterprise.context.ApplicationScoped
	public MessageInterpolator getInstance() {
		return interpolator;
	}

}
