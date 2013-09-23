package br.com.caelum.vraptor.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class LRUCapacityFactory {

	@Produces @LRUCapacity
	private int capacity = 100;
}
