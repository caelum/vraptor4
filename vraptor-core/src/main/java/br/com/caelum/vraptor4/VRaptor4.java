package br.com.caelum.vraptor4;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class VRaptor4 implements Filter{
	
	@Inject Logger logger;

	@Inject private BeanManagerUtil beanManagerUtil;
	
	@Inject
	private StupidRouter router;
	
	@Override
	public void destroy() {
	}

	@SuppressWarnings("serial")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (!(request instanceof HttpServletRequest) 
				|| !(response instanceof HttpServletResponse)) {
			
			throw new ServletException("VRaptor must be run inside a " +
				"Servlet environment. Portlets and others aren't supported.");
		}
		
		logger.debug("VRaptor received a new request");
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		Set<Bean<?>> beanControllers = beanManagerUtil.getBeans(Object.class,new AnnotationLiteral<Controller>() {});
		
		for (Bean beanController : beanControllers) {

			Object controller = beanManagerUtil.instanceFor(beanController);
			
			System.out.println("oi, to no for do filtro");
			
			Method method = router.respondTo(controller,httpRequest.getRequestURI());
			
			System.out.println("method bacanudo" + method);
			
			if(method!=null){
				try {
					method.invoke(controller);
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
		}
		
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
	}

}
