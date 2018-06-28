package br.com.caelum.vraptor.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.*;
import javax.inject.Inject;

import static br.com.caelum.vraptor.proxy.CDIProxies.*;
import static org.hamcrest.Matchers.equalTo;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class CDIProxiesTest {

	@Inject private AnyProxiableBean proxiable;
	@Inject private NonProxiableBean nonProxiable;

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
			.create(WebArchive.class)
				.addClass(AnyProxiableBean.class)
				.addClass(NonProxiableBean.class)
			.addAsManifestResource(INSTANCE, "beans.xml");
	}

	@Test
	public void shoulIdentifyCDIProxies() {
		assertTrue(isCDIProxy(proxiable.getClass()));
		assertFalse(isCDIProxy(nonProxiable.getClass()));
	}
	
	@Test
	public void shouldUnproxifyCDIProxies() {
		AnyProxiableBean bean = unproxifyIfPossible(proxiable);
		assertFalse(isCDIProxy(bean.getClass()));
	}

	@Test
	public void shouldReturnTheBeanIfItsNotCDIProxy() {
		NonProxiableBean bean = unproxifyIfPossible(nonProxiable);
		assertThat(bean, equalTo(nonProxiable));
	}	
}

@RequestScoped class AnyProxiableBean {}
@Dependent class NonProxiableBean {}
