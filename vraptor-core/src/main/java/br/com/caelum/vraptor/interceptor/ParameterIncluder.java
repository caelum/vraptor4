package br.com.caelum.vraptor.interceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.events.IncludeParameterEvent;
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

	public void include(@Observes IncludeParameterEvent event) {
		outjector.get().outjectRequestMap();
	}
}