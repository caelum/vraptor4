package br.com.caelum.vraptor.controller;

import br.com.caelum.vraptor.ioc.fixture.ControllerInTheClasspath;
import br.com.caelum.vraptor.proxy.CDIProxies;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DefaultControllerInstanceTest {

	@Inject
	private ControllerInTheClasspath controller;

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
			.create(WebArchive.class)
				.addClass(ControllerInTheClasspath.class)
				.addClass(ControllerInstance.class)
			.addAsManifestResource(INSTANCE, "beans.xml");
	}

	@Test
	public void shouldUnwrapCDIProxyFromControllerType() {
		ControllerInstance controllerInstance = controllerInstance();
		assertTrue(CDIProxies.isCDIProxy(controller.getClass()));
		BeanClass beanClass = controllerInstance.getBeanClass();
		assertFalse(CDIProxies.isCDIProxy(beanClass.getType()));
	}

	private DefaultControllerInstance controllerInstance() {
		return new DefaultControllerInstance(controller);
	}
}
