package br.com.caelum.vraptor.ioc.cdi;

import static br.com.caelum.vraptor.config.BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME;
import static br.com.caelum.vraptor.config.BasicConfiguration.SCANNING_PARAM;
import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.net.URLClassLoader;
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
		when(context.getInitParameter(BASE_PACKAGES_PARAMETER_NAME)).thenReturn("br.com.caelum.vraptor4.ioc.fixture");
		when(context.getRealPath("/WEB-INF/classes")).thenReturn(getClassDir());

		when(context.getClassLoader()).thenReturn(
				new URLClassLoader(new URL[] {Object.class.getResource("/test-fixture.jar")},
						currentThread().getContextClassLoader()));

        //allowing(context).getInitParameter(ENCODING);
        //allowing(context).setAttribute(with(any(String.class)), with(any(Object.class)));

        when(context.getInitParameter(SCANNING_PARAM)).thenReturn("enabled");
		configureExpectations(context);
		return context;
	}

    private void configureExpectations(ServletContext context) {
    	Enumeration<String> emptyEnumeration = Collections.enumeration(Collections.<String>emptyList());
    	when(context.getInitParameterNames()).thenReturn(emptyEnumeration);
    	when(context.getAttributeNames()).thenReturn(emptyEnumeration);
   }

	private String getClassDir() {
		return getClass().getResource("/br/com/caelum/vraptor4/test").getFile();
	}
}
