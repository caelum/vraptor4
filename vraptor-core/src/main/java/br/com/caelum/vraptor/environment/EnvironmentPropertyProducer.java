package br.com.caelum.vraptor.environment;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;


/**
 * A producer that allows us to inject environment properties. i.e
 * 
 * 	<pre>
 * 		<code>
 * 			@Inject @Qualifier("mail.server.host") 
 * 			private String mailHost;
 * 		</code>
 * 	</pre>
 * 
 * @author Rodrigo Turini
 * @since 4.0
 */
@ApplicationScoped
public class EnvironmentPropertyProducer {

	private final Environment environment;

	/**
	 * @deprecated CDI eyes only
	 */
	public EnvironmentPropertyProducer() {
		this(null);
	}
	
	@Inject
	public EnvironmentPropertyProducer(Environment environment) {
		this.environment = environment;
	}
	
	@Produces @Property
	public String get(InjectionPoint ip) {
		Annotated annotated = ip.getAnnotated();
		Property property = annotated.getAnnotation(Property.class);
		String key = property.value();
		if (isNullOrEmpty(key)) {
			key = ip.getMember().getName();
		}
		return environment.get(key);
	}
}