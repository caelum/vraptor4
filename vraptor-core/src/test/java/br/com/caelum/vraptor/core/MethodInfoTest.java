package br.com.caelum.vraptor.core;

import static br.com.caelum.vraptor.controller.DefaultControllerMethod.instanceFor;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.ValuedParameter;
import br.com.caelum.vraptor.view.DogController;

public class MethodInfoTest {

	private MethodInfo methodInfo;

	@Before
	public void setUp() {
		this.methodInfo = new MethodInfo(new ParanamerNameProvider());
	}
	
	@Test
	public void should_clear_valued_parameters_when_controller_method_is_updated() {
		ValuedParameter[] parameters = null;
		methodInfo.setControllerMethod(controllerMethod("bark", int.class));
		parameters = methodInfo.getValuedParameters();
		assertThat(parameters[0].getName(), is("times"));
		methodInfo.setControllerMethod(controllerMethod("bark", String.class));
		parameters = methodInfo.getValuedParameters();
		assertThat(parameters[0].getName(), is("phrase"));
	}

	private ControllerMethod controllerMethod(String methodName, Class<?> clazz) {
		return instanceFor(DogController.class, method(methodName, clazz));
	}

	private Method method(String methodName, Class<?> clazz) {
		try {
			return DogController.class.getMethod(methodName, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
