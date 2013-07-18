package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4x.Accepts;
import br.com.caelum.vraptor4x.AfterCall;
import br.com.caelum.vraptor4x.AroundCall;
import br.com.caelum.vraptor4x.BeforeCall;
import br.com.caelum.vraptor4x.interceptor.AcceptsWithAnnotations;
import br.com.caelum.vraptor4x.interceptor.CustomAcceptsFailCallback;
import br.com.caelum.vraptor4x.interceptor.SimpleInterceptorStack;

@Intercepts
@AcceptsWithAnnotations(NotLogged.class)
public class InterceptorWithCustomizedAccepts {
	
	private boolean interceptCalled;
	private boolean beforeCalled;
	private boolean afterCalled;

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		this.interceptCalled = true;
	}

	@BeforeCall
	public void before() {
		this.beforeCalled = true;
	}

	@AfterCall
	public void after() {
		this.afterCalled = true;
	}

	public boolean isInterceptCalled() {
		return interceptCalled;
	}

	public boolean isBeforeCalled() {
		return beforeCalled;
	}

	public boolean isAfterCalled() {
		return afterCalled;
	}

	@CustomAcceptsFailCallback
	public void customAcceptsFailCallback() {
	}
	
	
}
