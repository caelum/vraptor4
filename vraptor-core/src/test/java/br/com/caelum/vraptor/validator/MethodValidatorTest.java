package br.com.caelum.vraptor.validator;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.Valid;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.beanvalidation.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.beanvalidation.MethodValidatorFactoryCreator;
import br.com.caelum.vraptor.validator.beanvalidation.MethodValidatorInterceptor;
import br.com.caelum.vraptor.validator.beanvalidation.ValidatorFactoryCreator;

/**
 * Test method validator feature.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5
 */
public class MethodValidatorTest {

    @Mock private Localization l10n;
    @Mock private InterceptorStack stack;

    private MethodValidatorInterceptor interceptor;
    private ParameterNameProvider provider;
    private Validator validator;
    private ValidatorFactory factory;
    private MessageInterpolator interpolator;
    
	private ControllerMethod withConstraint;
	private ControllerMethod withTwoConstraints;
	private ControllerMethod withoutConstraint;
	private ControllerMethod cascadeConstraint;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        Locale.setDefault(Locale.ENGLISH);

        ValidatorFactoryCreator creator = new ValidatorFactoryCreator();
        creator.buildFactory();

        provider = new ParanamerNameProvider();
        
        MethodValidatorFactoryCreator methodValidatorCreator = new MethodValidatorFactoryCreator(provider);
        methodValidatorCreator.buildFactory();
        factory = methodValidatorCreator.getInstance();

        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(creator.getInstance());
        interpolatorFactory.createInterpolator();
        interpolator = interpolatorFactory.getInstance();

        validator = new MockValidator();
        
        withConstraint = DefaultControllerMethod.instanceFor(MyController.class, MyController.class.getMethod("withConstraint", String.class));
        withTwoConstraints = DefaultControllerMethod.instanceFor(MyController.class, MyController.class.getMethod("withTwoConstraints", String.class, Customer.class));
        withoutConstraint = DefaultControllerMethod.instanceFor(MyController.class, MyController.class.getMethod("withoutConstraint", String.class));
        cascadeConstraint = DefaultControllerMethod.instanceFor(MyController.class, MyController.class.getMethod("cascadeConstraint", Customer.class));
    }
    
    @Test
    public void shouldAcceptIfMethodHasConstraint() {
        interceptor = new MethodValidatorInterceptor(null, null, null, null, factory.getValidator());
    	assertThat(interceptor.accepts(withConstraint), is(true));
    	
        interceptor = new MethodValidatorInterceptor(null, null, null, null, factory.getValidator());
    	assertThat(interceptor.accepts(withTwoConstraints), is(true));
    	
        interceptor = new MethodValidatorInterceptor(null, null, null, null, factory.getValidator());
    	assertThat(interceptor.accepts(cascadeConstraint), is(true));
    }

    @Test
    public void shouldNotAcceptIfMethodHasConstraint() {
        interceptor = new MethodValidatorInterceptor(null, null, null, null, factory.getValidator());
    	assertThat(interceptor.accepts(withoutConstraint), is(false));
    }

    @Test
    public void shouldValidateMethodWithConstraint()
        throws Exception {
        MethodInfo info = new MethodInfo();
        info.setParameters(new Object[] { null });
        info.setControllerMethod(withConstraint);

        interceptor = new MethodValidatorInterceptor(l10n, interpolator, validator, info, factory.getValidator());
        when(l10n.getLocale()).thenReturn(new Locale("pt", "br"));

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getControllerMethod(), controller);
        
        assertThat(validator.getErrors(), hasSize(1));
        assertThat(validator.getErrors().get(0).getCategory(), is("withConstraint.email"));
    }

    @Test
    public void shouldUseDefaultLocale()
        throws Exception {
        MethodInfo info = new MethodInfo();
        info.setParameters(new Object[] { null });
        info.setControllerMethod(withConstraint);

        interceptor = new MethodValidatorInterceptor(l10n, interpolator, validator, info, factory.getValidator());

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getControllerMethod(), controller);

        assertThat(validator.getErrors(), hasSize(1));
        assertThat(validator.getErrors().get(0).getCategory(), is("withConstraint.email"));
        assertThat(validator.getErrors().get(0).getMessage(), is("may not be null"));
    }

    @Test
    public void shouldValidateMethodWithTwoConstraints()
        throws Exception {
        MethodInfo info = new MethodInfo();
        info.setParameters(new Object[] { null, new Customer(null, null) });
        info.setControllerMethod(withTwoConstraints);

        interceptor = new MethodValidatorInterceptor(l10n, interpolator, validator, info, factory.getValidator());
        when(l10n.getLocale()).thenReturn(new Locale("pt", "br"));

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getControllerMethod(), controller);
        String messages = validator.getErrors().toString();

        assertThat(validator.getErrors(), hasSize(3));
        
        assertThat(messages, containsString("não pode ser nulo"));
        assertThat(messages, containsString("withTwoConstraints.name"));
        assertThat(messages, containsString("withTwoConstraints.customer.name"));
        assertThat(messages, containsString("withTwoConstraints.customer.id"));
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

        public void withConstraint(@NotNull String email) {

        }

        public void withTwoConstraints(@NotNull String name, @Valid Customer customer) {

        }
        
        public void withoutConstraint(@Foo String foo) {
        	
        }
        
        public void cascadeConstraint(@Valid Customer customer) {

        }
    }
    
	@Target(value = { PARAMETER })
	@Retention(value = RUNTIME)
	@Documented
    public @interface Foo {
    	
    }
}
