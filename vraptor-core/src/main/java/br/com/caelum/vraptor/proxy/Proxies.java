package br.com.caelum.vraptor.proxy;

public final class Proxies {

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
}
