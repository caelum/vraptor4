package br.com.caelum.vraptor;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Simple Junit class runner that initialize weld container
 */
public class WeldJunitRunner extends BlockJUnit4ClassRunner {

	private Class<?> clazz;
	private static WeldContainer weldContainer;
	
	static {
		weldContainer = new Weld().initialize();
	}

	public WeldJunitRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		this.clazz = testClass;
	}

	/**
	 * With this, your test class is a CDI bean, so you can use DI
	 */
	@Override
	protected Object createTest() throws Exception {
		return weldContainer.instance().select(clazz).get();
	}
}