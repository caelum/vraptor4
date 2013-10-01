package br.com.caelum.vraptor.musicjungle.custom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Manages {@link EntityManagerFactory} creation.
 */
@ApplicationScoped
public class EntityManagerFactoryCreator {

	/**
	 * Produces an instance for {@link EntityManagerFactory}.
	 */
	@Produces
	@ApplicationScoped
	public EntityManagerFactory getInstance() {
		return Persistence.createEntityManagerFactory("default");
	}

	/**
	 * Closes {@link EntityManagerFactory}.
	 */
	public void close(@Disposes EntityManagerFactory entityManagerFactory) {
		entityManagerFactory.close();
	}

}
