package br.com.caelum.vraptor.proxy;

import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

/**
 * Utility class to work with CDI proxies.
 * 
 * @author Otávio Scherer Garcia
 * @author Mario Amaral
 */
public final class CDIProxies {

	private static final Class<?> weldProxyClass;

	static {
		Class<?> temp;
		try {
			temp = Class.forName("org.jboss.weld.bean.proxy.ProxyObject");
		} catch (ClassNotFoundException e) {
			temp = null;
		}
		weldProxyClass = temp;
	}

	public static boolean isCDIProxy(Class<?> type) {
		return weldProxyClass != null && weldProxyClass.isAssignableFrom(type);
	}

	public static <T> Class<?> extractRawType(Class<T> type) {
		return isCDIProxy(type) ? type.getSuperclass() : type;
	}

	@SuppressWarnings("unchecked")
	public static <T> T unwrap(Class<T> clazz) {
		BeanManager manager = CDI.current().getBeanManager();

		Set<Bean<?>> beans = manager.getBeans(clazz);
		if (beans.isEmpty()) {
			return null; //maybe throw exception ?
		}

		Bean<T> bean = (Bean<T>) beans.iterator().next();
		Context context = manager.getContext(bean.getScope()); 
		
		T beanInstance = context.get(bean);
		if (beanInstance != null) {
			return beanInstance;
		}

		CreationalContext<T> cc = manager.createCreationalContext(bean);
		return context.get(bean, cc);
	}
}
