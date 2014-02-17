package br.com.caelum.vraptor.validator;

import static br.com.caelum.vraptor.controller.DefaultControllerMethod.instanceFor;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerInstance;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.factory.Factories;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.beanvalidation.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.beanvalidation.MethodValidator;

/**
 * Test method validator feature.
 *
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 * @since 3.5
 */
public class MethodValidatorTest {

	private Validator validator;
	private ValidatorFactory validatorFactory;
	private MessageInterpolator interpolator;

	private ControllerMethod withConstraint;
	private ControllerMethod withoutConstraint;
	private DefaultControllerInstance instance;

	private MethodInfo methodInfo = new MethodInfo(Factories.createParameterNameProvider());

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		Locale.setDefault(Locale.ENGLISH);
		validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory();
		interpolator = new MessageInterpolatorFactory(validatorFactory).getInstance();
		validator = new MockValidator();
		withConstraint = instanceFor(MyController.class, getMethod("withConstraint"));
		withoutConstraint = instanceFor(MyController.class, getMethod("withoutConstraint"));
		instance = new DefaultControllerInstance(new MyController());
	}

	private Method getMethod(String methodName) throws NoSuchMethodException {
		return MyController.class.getMethod(methodName, String.class);
	}

	@Test
	public void shouldAcceptIfMethodHasConstraint() {
		methodInfo.setControllerMethod(withConstraint);

		DefaultControllerInstance controller = spy(instance);
		getMethodValidator().validate(new ReadyToExecuteMethod(withConstraint), controller, methodInfo, validator);
		verify(controller).getController();
	}

	@Test
	public void shouldNotAcceptIfMethodHasConstraint() {
		DefaultControllerInstance controller = spy(instance);
		getMethodValidator().validate(new ReadyToExecuteMethod(withoutConstraint), controller, methodInfo, validator);
		verify(controller, never()).getController();
	}

	@Test
	public void shouldValidateMethodWithConstraint() throws Exception {
		methodInfo.setControllerMethod(withConstraint);

		getMethodValidator().validate(new ReadyToExecuteMethod(withConstraint), instance, methodInfo, validator);
		assertThat(validator.getErrors(), hasSize(1));
		assertThat(validator.getErrors().get(0).getCategory(), is("withConstraint.email"));
	}

	private MethodValidator getMethodValidator() {
		return new MethodValidator(new Locale("pt", "br"), interpolator, validatorFactory.getValidator());
	}

	/**
	 * Customer for using in bean validator tests.
	 */
	public class Customer {

		@NotNull public Integer id;
		@NotNull public String name;

		public Customer(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	public class MyController {
		public void withConstraint(@NotNull String email) { }
		public void withoutConstraint(String foo) { }
	}
}