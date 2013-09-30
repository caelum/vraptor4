package br.com.caelum.vraptor.musicjungle.custom;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;

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
	 * Closes {@link Session}.
	 */
	public void close(@Disposes EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}

}
