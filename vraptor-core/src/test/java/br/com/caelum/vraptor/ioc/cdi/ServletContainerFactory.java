package br.com.caelum.vraptor.ioc.cdi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Enumeration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

@ApplicationScoped
public class ServletContainerFactory {

	private int counter;

	public HttpSessionMock getSession() throws Exception {
		return new HttpSessionMock(createServletContext(), "session" + ++counter);
	}

	public HttpServletRequestMock getRequest() throws Exception {
		return new HttpServletRequestMock(getSession(), mock(
				MutableRequest.class, "request" + counter));
	}

	public MutableResponse getResponse() {
		return mock(MutableResponse.class, "response" + counter);
	}

	public FilterChain getFilterChain() {
		return mock(FilterChain.class);
	}


	@Produces
	@Default
	public ServletContext createServletContext() {
		ServletContext context = mock(ServletContext.class, "servlet context");

		when(context.getMajorVersion()).thenReturn(3);
		when(context.getRealPath("/WEB-INF/classes")).thenReturn(getClassDir());

		configureExpectations(context);
		return context;
	}

	private void configureExpectations(ServletContext context) {
		Enumeration<String> emptyEnumeration = Collections.enumeration(Collections.<String>emptyList());
		when(context.getInitParameterNames()).thenReturn(emptyEnumeration);
		when(context.getAttributeNames()).thenReturn(emptyEnumeration);
   }

	private String getClassDir() {
		return getClass().getResource("/br/com/caelum/vraptor/test").getFile();
	}
}
