package br.com.caelum.vraptor4.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.Producer;
import javax.servlet.ServletContext;

import org.junit.Ignore;

import br.com.caelum.vraptor4.ioc.GenericContainerTest;
import br.com.caelum.vraptor4.ioc.MySessionComponent;
import br.com.caelum.vraptor4.ioc.TheComponentFactory;
import br.com.caelum.vraptor4.ioc.components.DummyComponentFactory;
import br.com.caelum.vraptor4.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.ControllerInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor4.ioc.fixture.InterceptorInTheClasspath;

@Ignore
public class TestExtension implements Extension{
	
	public void beforeBeanDiscovey(@Observes BeforeBeanDiscovery discovery, BeanManager bm) {
		//just test objects
		discovery.addAnnotatedType(bm.createAnnotatedType(DummyComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ComponentFactoryInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ControllerInTheClasspath.class));
		
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyAppComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyAppComponentWithLifecycle.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyRequestComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyDependentComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.DisposableComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.StartableComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(InterceptorInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ConverterInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(CustomComponentInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(CustomComponentWithLifecycleInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(MySessionComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(DependentOnSomethingFromComponentFactory.class));
	}	
	
	public void processProducerForServletContext(@Observes ProcessProducer<ServletContextFactory,ServletContext> producer){
		final Producer<ServletContext> defaultProducer = producer.getProducer();
		Producer<ServletContext> testProducer = new Producer<ServletContext>(){

			public ServletContext produce(CreationalContext<ServletContext> ctx) {
				return new ServletContainerFactory().createServletContext();
			}

			public void dispose(ServletContext instance) {
				
			}

			public Set<InjectionPoint> getInjectionPoints() {
				return defaultProducer.getInjectionPoints();
			}
			
		};

		producer.setProducer(testProducer);
	}
	

}
