package br.com.caelum.vraptor.validator;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.AccessibleObject;
import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.beanvalidation.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.beanvalidation.MethodValidatorInterceptor;

/**
 * Test method validator feature.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5
 */
public class MethodValidatorTest {

	@Mock private InterceptorStack stack;

	private MethodValidatorInterceptor interceptor;
	private ParameterNameProvider provider;
	private Validator validator;
	private ValidatorFactory validatorFactory;
	private MessageInterpolator interpolator;
	
	private ControllerMethod withConstraint;
	private ControllerMethod withoutConstraint;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		Locale.setDefault(Locale.ENGLISH);

		provider = new ParanamerNameProvider(new DefaultCacheStore<AccessibleObject, Parameter[]>());
		
		validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory();

		MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(validatorFactory);
		interpolator = interpolatorFactory.getInstance();

		validator = new MockValidator();
		
		withConstraint = DefaultControllerMethod.instanceFor(MyController.class, MyController.class.getMethod("withConstraint", String.class));
		withoutConstraint = DefaultControllerMethod.instanceFor(MyController.class, MyController.class.getMethod("withoutConstraint", String.class));
	}
	
	@Test
	public void shouldAcceptIfMethodHasConstraint() {
		interceptor = new MethodValidatorInterceptor(null, null, null, null, validatorFactory.getValidator(), null);
		
		assertThat(interceptor.accepts(withConstraint), is(true));
	}

	@Test
	public void shouldNotAcceptIfMethodHasConstraint() {
		interceptor = new MethodValidatorInterceptor(null, null, null, null, validatorFactory.getValidator(), null);
		assertThat(interceptor.accepts(withoutConstraint), is(false));
	}

	@Test
	public void shouldValidateMethodWithConstraint()
		throws Exception {
		MethodInfo info = new MethodInfo();
		info.setParameters(new Object[] { null });
		info.setControllerMethod(withConstraint);

		interceptor = new MethodValidatorInterceptor(new Locale("pt", "br"), interpolator, validator, info, 
				validatorFactory.getValidator(), provider);

		MyController controller = new MyController();
		interceptor.intercept(stack, info.getControllerMethod(), controller);
		
		assertThat(validator.getErrors(), hasSize(1));
		assertThat(validator.getErrors().get(0).getCategory(), is("withConstraint.email"));
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
