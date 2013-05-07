package br.com.caelum.vraptor4;

import java.io.IOException;
import java.lang.reflect.Method;

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
	
	@Inject private Logger logger;

	@Inject private BeanManagerUtil beanManagerUtil;
	
	@Inject private StupidRouter router;
	
	@Inject private ScannedControllers scannedControllers;
	
	@Override
	public void destroy() {
	}

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
		
		for (Class<?>  controller: scannedControllers.getClasses()) {
			
			Method method = router.respondTo(controller,httpRequest.getRequestURI());
			
			if(method!=null){
				try {
					method.invoke(beanManagerUtil.instanceFor(controller));
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
