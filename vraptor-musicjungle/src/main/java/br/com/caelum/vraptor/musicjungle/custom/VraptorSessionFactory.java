package br.com.caelum.vraptor.musicjungle.custom;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;


public class VraptorSessionFactory {
	
	private static SessionFactory sessionFactory;

	@PostConstruct
    public void create() {
        Configuration cfg = new Configuration().configure(getHibernateCfgLocation());
        ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
        ServiceRegistry serviceRegistry = builder.applySettings(cfg.getProperties()).buildServiceRegistry();
        sessionFactory = cfg.buildSessionFactory(serviceRegistry);
        
    }
	
	@Produces
	public Session getSession(){
		return sessionFactory.openSession();
	}
	
	public void close(@Disposes Session session){
		if (session.isOpen()) {
            session.close();
        }
	}

    private URL getHibernateCfgLocation() {
        return getClass().getResource(getHibernateCfgName());
    }

    private String getHibernateCfgName() {
        return "/hibernate.cfg.xml";
    }
	
}
