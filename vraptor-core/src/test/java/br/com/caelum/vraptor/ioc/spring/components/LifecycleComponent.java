package br.com.caelum.vraptor.ioc.spring.components;

import javax.annotation.PostConstruct;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class LifecycleComponent {

	public static boolean initialized;

	@PostConstruct
	public void init() {
		initialized = true;
	}
}
