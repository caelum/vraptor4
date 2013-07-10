package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultControllerInstance;

public class AspectStyleInterceptorHandler implements InterceptorHandler {

	private StepInvoker stepInvoker;
	private Container container;
	private Class<?> interceptorClass;
	private static final Logger logger = LoggerFactory
			.getLogger(AspectStyleInterceptorHandler.class);
	private InterceptorMethodParametersResolver parametersResolver;
	private ControllerInstance controllerInstance;


	public AspectStyleInterceptorHandler(Class<?> interceptorClass,
			StepInvoker stepInvoker, Container container) {
		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;
		parametersResolver = new InterceptorMethodParametersResolver(
				stepInvoker, container);
		this.controllerInstance = container.instanceFor(ControllerInstance.class);
		configure();

	}

	private void configure() {
		Method acceptsMethod = stepInvoker.findMethod(Accepts.class,
				interceptorClass);
		InternalAcceptsSignature internalAcceptsSignature = new InternalAcceptsSignature(new NoStackParameterSignatureAcceptor());
		boolean interceptorAccepts = false;
		if (acceptsMethod != null) {
			if(!internalAcceptsSignature.accepts(acceptsMethod)){
				throw new VRaptorException(internalAcceptsSignature.errorMessage());
			}
			interceptorAccepts = true;
		}
		List<Annotation> customAnnotations = CustomAcceptsVerifier.getCustomAcceptsAnnotations(interceptorClass);
		boolean customAccepts = false;
		if(!customAnnotations.isEmpty()){
			customAccepts = true;
		}
		if(customAccepts && interceptorAccepts){
			throw new VRaptorException("Interceptor "+interceptorClass+" must declare internal accepts or custom, not both.");
		}
		
		
		
		Method around = stepInvoker.findMethod(AroundCall.class, interceptorClass);
		if(around!=null){
			MustReceiveStackAsParameterAcceptor stackAcceptor = new MustReceiveStackAsParameterAcceptor();
			if(!stackAcceptor.accepts(around)){
				throw new IllegalArgumentException(stackAcceptor.errorMessage());
			}
		}
		
		NoStackParameterSignatureAcceptor noStackAcceptor = new NoStackParameterSignatureAcceptor();
		Method before = stepInvoker.findMethod(BeforeCall.class, interceptorClass);
		if(before!=null){
			if(!noStackAcceptor.accepts(before)){
				throw new IllegalArgumentException(noStackAcceptor.errorMessage());
			}
		}
		
		Method after = stepInvoker.findMethod(AfterCall.class, interceptorClass);
		if(after!=null){
			if(!noStackAcceptor.accepts(after)){
				throw new IllegalArgumentException(noStackAcceptor.errorMessage());
			}
		}

	}

	public void execute(InterceptorStack stack,
			ControllerMethod controllerMethod, Object currentController) {
		Object interceptor = container.instanceFor(interceptorClass);

		logger.debug("Invoking interceptor {}", interceptor.getClass()
				.getSimpleName());

		boolean customAccepts = new CustomAcceptsExecutor(stepInvoker,
				container).execute(interceptor, controllerMethod,
				controllerInstance);
		boolean interceptorAccepts = new InterceptorAcceptsExecutor(
				stepInvoker, parametersResolver).execute(interceptor);
		if (customAccepts && interceptorAccepts) {
			stepInvoker.tryToInvoke(interceptor, BeforeCall.class);
			new AroundExecutor(stepInvoker, stack, parametersResolver).execute(
					interceptor, controllerMethod, controllerInstance);
			stepInvoker.tryToInvoke(interceptor, AfterCall.class);
		} else {
			stack.next(controllerMethod, controllerInstance.getController());
		}

	}

}
