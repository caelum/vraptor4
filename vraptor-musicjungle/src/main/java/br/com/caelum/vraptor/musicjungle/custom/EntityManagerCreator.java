package br.com.caelum.vraptor.musicjungle.custom;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@RequestScoped
public class EntityManagerCreator {

	@Inject
	private EntityManagerFactory factory;

	/**
	 * Create an instance for {@link EntityManager}.
	 */
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return factory.createEntityManager();
	}

	/**
	 * Closes {@link EntityManager}.
	 */
	public void close(@Disposes EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}

}
