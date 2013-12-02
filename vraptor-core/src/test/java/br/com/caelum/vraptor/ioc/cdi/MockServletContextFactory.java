package br.com.caelum.vraptor.ioc.cdi;

import static org.mockito.Mockito.mock;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.interceptor.Interceptor;
import javax.servlet.ServletContext;

@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
@Specializes
class MockServletContextFactory extends ServletContextFactory {

	@Override @Produces
	public ServletContext getInstance() {
		return mock(ServletContext.class);
	}
}