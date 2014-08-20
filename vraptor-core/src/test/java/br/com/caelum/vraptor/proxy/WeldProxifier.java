package br.com.caelum.vraptor.proxy;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class WeldProxifier {

	private static WeldContainer weld;

	static {
		weld = new Weld().initialize();
	}
	
	public <T> T proxify(Class<T> clazz) {
		return weld.instance().select(clazz).get();
	}

}
