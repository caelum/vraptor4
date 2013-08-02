package br.com.caelum.vraptor.musicjungle.custom;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class VRaptorSessionFactory {

	private static SessionFactory sessionFactory;

	static {
		Configuration cfg = new Configuration().configure(VRaptorSessionFactory.class.getResource("/hibernate.cfg.xml"));
		ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
		ServiceRegistry serviceRegistry = builder.applySettings(cfg.getProperties()).buildServiceRegistry();
		sessionFactory = cfg.buildSessionFactory(serviceRegistry);
	}

	@Produces
	@RequestScoped
	public Session getSession() {
		return sessionFactory.openSession();
	}

	public void close(@Disposes Session session) {
		if (session.isOpen()) {
			session.close();
		}
	}

}
