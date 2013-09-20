package br.com.caelum.vraptor.musicjungle.custom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Manages {@link SessionFactory} creation.
 */
@ApplicationScoped
public class SessionFactoryCreator {

    /**
     * Produces an instance for {@link SessionFactory}.
     */
    @Produces
    @ApplicationScoped
    public SessionFactory getInstance() {
        Configuration cfg = new Configuration().configure(getClass().getResource("/hibernate.cfg.xml"));
        ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
        ServiceRegistry serviceRegistry = builder.applySettings(cfg.getProperties()).buildServiceRegistry();
        return cfg.buildSessionFactory(serviceRegistry);
    }

    /**
     * Closes {@link SessionFactory}.
     */
    public void close(@Disposes SessionFactory sessionFactory) {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
