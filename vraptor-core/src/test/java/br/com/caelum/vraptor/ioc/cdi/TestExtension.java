package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.Producer;
import javax.servlet.ServletContext;

import org.junit.Ignore;

@Ignore
public class TestExtension implements Extension{


	public void processProducerForServletContext(@Observes ProcessProducer<ServletContextFactory,ServletContext> producer){
		final Producer<ServletContext> defaultProducer = producer.getProducer();
		Producer<ServletContext> testProducer = new Producer<ServletContext>(){

			@Override
			public ServletContext produce(CreationalContext<ServletContext> ctx) {
				return new ServletContainerFactory().createServletContext();
			}

			@Override
			public void dispose(ServletContext instance) {

			}

			@Override
			public Set<InjectionPoint> getInjectionPoints() {
				return defaultProducer.getInjectionPoints();
			}

		};

		producer.setProducer(testProducer);
	}


}
