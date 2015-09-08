package br.com.caelum.vraptor.controller;

import br.com.caelum.vraptor.*;
import br.com.caelum.vraptor.ioc.fixture.ControllerInTheClasspath;
import br.com.caelum.vraptor.proxy.CDIProxies;
import org.junit.*;
import org.junit.runner.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(WeldJunitRunner.class)
public class DefaultControllerInstanceTest {

	@Inject
	private ControllerInTheClasspath controller;

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