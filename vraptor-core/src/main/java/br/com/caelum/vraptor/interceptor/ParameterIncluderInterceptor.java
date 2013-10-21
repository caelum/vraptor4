package br.com.caelum.vraptor.interceptor;

import javax.inject.Inject;

import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.validator.Outjector;

/**
 * Interceptor that includes all the parameters on the view of
 * a method annotated with {@link IncludeParameters} annotation
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@Intercepts(after=ParametersInstantiatorInterceptor.class)
@AcceptsWithAnnotations(IncludeParameters.class)
public class ParameterIncluderInterceptor {

	private Outjector outjector;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected ParameterIncluderInterceptor() {
		this(null);
	}

	@Inject
	public ParameterIncluderInterceptor(Outjector outjector) {
		this.outjector = outjector;
	}

	@BeforeCall
	public void intercept() {
		outjector.outjectRequestMap();
	}
}