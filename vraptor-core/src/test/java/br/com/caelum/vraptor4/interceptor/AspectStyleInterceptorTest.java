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
	public void sempreChamaInterceptParaInterceptorSemAccepts(){
		AlwaysAcceptsAspectInterceptor interceptor = spy(new AlwaysAcceptsAspectInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker);
		handler.hanle();
		verify(interceptor).intercept();
	}
	
	@Test
	public void naoChamaInterceptCasoNaoTenhaAroundInvoke(){
		InterceptorWithoutAroundInvoke interceptor = spy(new InterceptorWithoutAroundInvoke());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker);
		handler.hanle();
		verify(interceptor,never()).intercept();
	}
	
	@Test
	public void chamaOBeginAntesDoIntercept(){
		AlwaysAcceptsAspectInterceptor interceptor = spy(new AlwaysAcceptsAspectInterceptor());
		AspectHandler handler = new AspectHandler(interceptor,stepInvoker);
		handler.hanle();
		InOrder order = inOrder(interceptor);
		order.verify(interceptor).begin();		
		order.verify(interceptor).intercept();		
		order.verify(interceptor).after();		
	}
	
}
