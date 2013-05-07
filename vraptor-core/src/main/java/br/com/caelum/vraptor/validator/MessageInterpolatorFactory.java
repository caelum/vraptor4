/**
 *
 */
package br.com.caelum.vraptor.validator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Factory for JSR303 MessageInterpolator
 * @author Lucas Cavalcanti
 * @since 3.1.3
 *
 */
@Component
@ApplicationScoped
public class MessageInterpolatorFactory implements ComponentFactory<MessageInterpolator> {

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

	@Override
	public MessageInterpolator getInstance() {
		return interpolator;
	}

}
