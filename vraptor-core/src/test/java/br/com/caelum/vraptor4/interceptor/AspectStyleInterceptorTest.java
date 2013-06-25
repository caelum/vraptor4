package br.com.caelum.vraptor4.interceptor;

import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AspectStyleInterceptorTest {
	
	private StepInvoker stepInvoker = new StepInvoker();

	@Test
	public void shouldAlwaysCallAround(){
		AlwaysAcceptsAspectInterceptor interceptor = spy(new AlwaysAcceptsAspectInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker);
		handler.handle();
		verify(interceptor).intercept();
	}
	
	@Test
	public void shouldNotInvokeMethodIfDoesNotHaveAroundInvoke(){
		InterceptorWithoutAroundInvoke interceptor = spy(new InterceptorWithoutAroundInvoke());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker);
		handler.handle();
		verify(interceptor,never()).intercept();
	}
	
	@Test
	public void shouldInvokeUseBeforeAndAfter(){
		AlwaysAcceptsAspectInterceptor interceptor = spy(new AlwaysAcceptsAspectInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker);
		handler.handle();
		InOrder order = inOrder(interceptor);
		order.verify(interceptor).begin();		
		order.verify(interceptor).intercept();		
		order.verify(interceptor).after();		
	}
	
	@Test
	public void shouldInvokeIfAccepts(){
		AcceptsInterceptor acceptsInterceptor = spy(new AcceptsInterceptor(true));
		AspectHandler aspectHandler = new AspectHandler(acceptsInterceptor, stepInvoker);
		aspectHandler.handle();
		InOrder order = inOrder(acceptsInterceptor);
		order.verify(acceptsInterceptor).accepts();
		order.verify(acceptsInterceptor).before();
		order.verify(acceptsInterceptor).around();
		order.verify(acceptsInterceptor).after();
	}
	
	@Test
	public void shouldNotInvokeIfDoesNotAccept(){
		AcceptsInterceptor acceptsInterceptor = spy(new AcceptsInterceptor(false));
		AspectHandler aspectHandler = new AspectHandler(acceptsInterceptor, stepInvoker);
		aspectHandler.handle();
		verify(acceptsInterceptor).accepts();
		verify(acceptsInterceptor,never()).before();
		verify(acceptsInterceptor,never()).around();
		verify(acceptsInterceptor,never()).after();
	}
	
}
