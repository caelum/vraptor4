package br.com.caelum.vraptor.observer;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.interceptor.IncludeParameters;
import br.com.caelum.vraptor.validator.Outjector;

/**
 * Includes all the parameters on the view of a method
 * annotated with {@link IncludeParameters} annotation
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@ApplicationScoped
public class ParameterIncluder {

	private Instance<Outjector> outjector;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ParameterIncluder() {
		this(null);
	}

	@Inject
	public ParameterIncluder(Instance<Outjector> outjector) {
		this.outjector = outjector;
	}

	public void include(@Observes ReadyToExecuteMethod event) {
		Method method = event.getControllerMethod().getMethod();
		if (method.isAnnotationPresent(IncludeParameters.class)) {
			outjector.get().outjectRequestMap();
		}
	}
}