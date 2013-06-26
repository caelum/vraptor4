package br.com.caelum.vraptor4.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InstanceContainer;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultControllerInstance;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AspectStyleInterceptorTest {
	
	private StepInvoker stepInvoker = new StepInvoker();
	private @Mock InterceptorStack stack;
	private @Mock ControllerMethod controllerMethod;
	private @Mock ControllerInstance controllerInstance;
	
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

	@Test
	public void shouldAlwaysCallAround(){
		AlwaysAcceptsAspectInterceptor interceptor = spy(new AlwaysAcceptsAspectInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker,new InstanceContainer());
		handler.handle(stack,controllerMethod,controllerInstance);
		verify(interceptor).intercept(Mockito.any(InterceptorStack.class),Mockito.same(controllerMethod),Mockito.same(controllerInstance));
	}
	
	@Test
	public void shouldNotInvokeMethodIfDoesNotHaveAroundInvoke(){
		WithoutAroundInvokeInterceptor interceptor = spy(new WithoutAroundInvokeInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker,new InstanceContainer());
		handler.handle(stack,controllerMethod,controllerInstance);
		verify(interceptor,never()).intercept(stack,controllerMethod,controllerInstance);
	}
	
	@Test
	public void shouldInvokeUsingBeforeAndAfter(){
		AlwaysAcceptsAspectInterceptor interceptor = spy(new AlwaysAcceptsAspectInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker,new InstanceContainer());
		handler.handle(stack,controllerMethod,controllerInstance);
		InOrder order = inOrder(interceptor);
		order.verify(interceptor).begin();		
		order.verify(interceptor).intercept(Mockito.any(InterceptorStack.class),Mockito.same(controllerMethod),Mockito.same(controllerInstance));		
		order.verify(interceptor).after();		
	}
	
	@Test
	public void shouldInvokeIfAccepts(){
		AcceptsInterceptor acceptsInterceptor = spy(new AcceptsInterceptor(true));
		AspectHandler aspectHandler = new AspectHandler(acceptsInterceptor, stepInvoker,new InstanceContainer());
		aspectHandler.handle(stack,controllerMethod,controllerInstance);
		InOrder order = inOrder(acceptsInterceptor);
		order.verify(acceptsInterceptor).accepts(controllerMethod);
		order.verify(acceptsInterceptor).before();
		order.verify(acceptsInterceptor).around(Mockito.any(InterceptorStack.class),Mockito.same(controllerMethod),Mockito.same(controllerInstance));
		order.verify(acceptsInterceptor).after();
	}	
	
	@Test
	public void shouldNotInvokeIfDoesNotAccept(){
		AcceptsInterceptor acceptsInterceptor = spy(new AcceptsInterceptor(false));
		AspectHandler aspectHandler = new AspectHandler(acceptsInterceptor, stepInvoker,new InstanceContainer());
		aspectHandler.handle(stack,controllerMethod,controllerInstance);
		verify(acceptsInterceptor).accepts(controllerMethod);
		verify(acceptsInterceptor,never()).before();
		verify(acceptsInterceptor,never()).around(stack,controllerMethod,controllerInstance);
		verify(acceptsInterceptor,never()).after();
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldVerifyIfAcceptsMethodReturnsVoid(){
		VoidAcceptsInterceptor weirdInterceptor = new VoidAcceptsInterceptor();
		new AspectHandler(weirdInterceptor, stepInvoker,new InstanceContainer()).handle(stack, controllerMethod, controllerInstance);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldVerifyIfAcceptsMethodReturnsNonBooleanType(){
		NonBooleanAcceptsInterceptor weirdInterceptor = new NonBooleanAcceptsInterceptor();
		new AspectHandler(weirdInterceptor, stepInvoker,new InstanceContainer()).handle(stack, controllerMethod, controllerInstance);
	}
	
	@Test
	public void shouldInvokeAcceptsWithoutArgs(){
		AcceptsWithoutArgsInterceptor acceptsWithoutArgsInterceptor = spy(new AcceptsWithoutArgsInterceptor());
		AspectHandler aspectHandler = new AspectHandler(acceptsWithoutArgsInterceptor, stepInvoker,new InstanceContainer());
		aspectHandler.handle(stack,controllerMethod,controllerInstance);
		InOrder order = inOrder(acceptsWithoutArgsInterceptor);
		order.verify(acceptsWithoutArgsInterceptor).accepts();
		order.verify(acceptsWithoutArgsInterceptor).before();
		order.verify(acceptsWithoutArgsInterceptor).around(Mockito.any(InterceptorStack.class),Mockito.same(controllerMethod),Mockito.same(controllerInstance));
		order.verify(acceptsWithoutArgsInterceptor).after();
	}	
	
	@Test
	public void shouldInvokeAroundWithSimpleStack(){
		ExampleOfSimpleStackInterceptor simpleStackInterceptor = spy(new ExampleOfSimpleStackInterceptor());
		AspectHandler aspectHandler = new AspectHandler(simpleStackInterceptor, stepInvoker,new InstanceContainer());
		aspectHandler.handle(stack,controllerMethod,controllerInstance);		
		verify(simpleStackInterceptor).around(Mockito.any(DefaultSimplerInterceptorStack.class));
	}	
	
	@Test
	public void shouldInvokeNextIfNotAccepts() throws Exception {
		AcceptsInterceptor interceptor = spy(new AcceptsInterceptor(false));
		AspectHandler aspectHandler = new AspectHandler(interceptor, stepInvoker, new InstanceContainer());
		ControllerInstance instance = new DefaultControllerInstance(null);
		aspectHandler.handle(stack, controllerMethod, instance);
		verify(interceptor, never()).around(stack, controllerMethod, controllerInstance);
		verify(stack).next(controllerMethod, instance.getController());		
	}
	
	@Test
	public void shouldInvokeNotIfDoesNotHaveAround() throws Exception {
		WithoutAroundInterceptor interceptor = spy(new WithoutAroundInterceptor());
		AspectHandler aspectHandler = new AspectHandler(interceptor, stepInvoker, new InstanceContainer());
		ControllerInstance instance = new DefaultControllerInstance(null);
		aspectHandler.handle(stack, controllerMethod, instance);		
		verify(stack).next(controllerMethod, instance.getController());
	}
	
}