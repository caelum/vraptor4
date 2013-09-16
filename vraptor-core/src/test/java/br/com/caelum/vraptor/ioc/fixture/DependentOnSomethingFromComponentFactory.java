/**
 *
 */
package br.com.caelum.vraptor.ioc.fixture;

import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.NeedsCustomInstantiation;

public class DependentOnSomethingFromComponentFactory {
	private final NeedsCustomInstantiation dependency;

	@Inject
	public DependentOnSomethingFromComponentFactory(NeedsCustomInstantiation dependency) {
		this.dependency = dependency;
	}

	public NeedsCustomInstantiation getDependency() {
		return dependency;
	}
}