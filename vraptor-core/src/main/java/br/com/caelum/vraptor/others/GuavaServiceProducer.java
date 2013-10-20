package br.com.caelum.vraptor.others;

import static java.util.Collections.emptySet;

import java.util.Set;

import javax.enterprise.inject.Produces;

import com.google.common.util.concurrent.Service;

/**
 * Class to avoid erros when using in CDI environments. See 
 * https://code.google.com/p/guava-libraries/issues/detail?id=1433 for more details.
 */
public class GuavaServiceProducer {

	@Produces
	public Set<Service> guavaCdiClashWorkaround() {
		return emptySet();
	}
}
