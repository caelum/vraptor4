package br.com.caelum.vraptor4.extensions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor4.ScannedControllers;

@ApplicationScoped
class ControllersFactory {

	@Produces @ApplicationScoped
	public ScannedControllers produce(ControllersExtension extension) {
		return extension.controllers;
	}

}