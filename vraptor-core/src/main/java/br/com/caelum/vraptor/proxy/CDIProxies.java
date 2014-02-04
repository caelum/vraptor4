package br.com.caelum.vraptor.proxy;

import org.jboss.weld.bean.proxy.ProxyObject;
import org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy;

/**
 * Utility class to work with CDI proxies, allowing us to get objects without weld proxies. At this time this
 * class only works with JBoss Weld, but since Weld is the only one that implements CDI 1.1, this is not a big
 * trouble.
 * 
 * @author Otávio Scherer Garcia
 * @author Mario Amaral
 */
public final class CDIProxies {

	public static boolean isCDIProxy(Class<?> type) {
		return ProxyObject.class.isAssignableFrom(type);
	}

	public static <T> Class<?> extractRawTypeIfPossible(Class<T> type) {
		return isCDIProxy(type) ? type.getSuperclass() : type;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T unproxifyIfPossible(T source) {
		if (source instanceof TargetInstanceProxy) {
			TargetInstanceProxy<T> target = (TargetInstanceProxy) source;
			return target.getTargetInstance();
		}
		return source;
	}
}
