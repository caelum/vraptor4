package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Listener to register {@link BeanManager} in {@link ServletContext} and fire event
 * to enable the Producer.
 * @author Alberto Souza and Mario Amaral
 *
 */
public class BeanManagerSetup implements ServletContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(BeanManagerSetup.class);

	@Inject
	private BeanManager beanManager;

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {
		if (event.getServletContext().getAttribute(CDIProvider.BEAN_MANAGER_KEY) == null) {
			logger.info("Registering BeanManager with key {} in ServletContext",CDIProvider.BEAN_MANAGER_KEY);
			event.getServletContext().setAttribute(CDIProvider.BEAN_MANAGER_KEY, beanManager);
		}
		beanManager.fireEvent(event.getServletContext());
	}

}
