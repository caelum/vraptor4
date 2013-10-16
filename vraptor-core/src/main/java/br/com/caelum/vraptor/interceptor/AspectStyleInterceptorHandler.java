package br.com.caelum.vraptor.interceptor;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;

public class AspectStyleInterceptorHandler implements InterceptorHandler {

	private final StepInvoker stepInvoker;
	private final Container container;
	private final Class<?> interceptorClass;
	private static final Logger logger = getLogger(AspectStyleInterceptorHandler.class);
	private final InterceptorMethodParametersResolver parametersResolver;
	private StepExecutor<Boolean> acceptsExecutor;
	private StepExecutor<?> after;
	private StepExecutor<?> around;
	private StepExecutor<?> before;
	private MirrorList<Method> interceptorMethods;

	public AspectStyleInterceptorHandler(Class<?> interceptorClass, StepInvoker stepInvoker,
			Container container, InterceptorMethodParametersResolver parametersResolver) {
		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;
		this.parametersResolver = parametersResolver;
		this.interceptorMethods = stepInvoker.findAllMethods(interceptorClass);
		configure();
	}

	private void configure() {

		after = new NoStackParameterStepExecutor(stepInvoker, find(AfterCall.class), interceptorClass);
		around = new AroundExecutor(stepInvoker,parametersResolver, find(AroundCall.class), interceptorClass);
		before = new NoStackParameterStepExecutor(stepInvoker, find(BeforeCall.class), interceptorClass);

		if(!after.accept(interceptorClass)) after = new DoNothingStepExecutor();
		if(!around.accept(interceptorClass)) around = new StackNextExecutor(container);
		if(!before.accept(interceptorClass)) before = new DoNothingStepExecutor();

		CustomAcceptsExecutor customAcceptsExecutor = new CustomAcceptsExecutor(stepInvoker, container, find(CustomAcceptsFailCallback.class), interceptorClass);
		InterceptorAcceptsExecutor interceptorAcceptsExecutor = new InterceptorAcceptsExecutor(stepInvoker, parametersResolver, find(Accepts.class), interceptorClass);
		boolean customAccepts = customAcceptsExecutor.accept(interceptorClass);
		boolean internalAccepts = interceptorAcceptsExecutor.accept(interceptorClass);
		if(customAccepts && internalAccepts){
			throw new VRaptorException("Interceptor "+interceptorClass+" must declare internal accepts or custom, not both.");
		}
		this.acceptsExecutor = customAccepts?customAcceptsExecutor:interceptorAcceptsExecutor;
	}

	private Method find(Class<? extends Annotation> step) {
		return stepInvoker.findMethod(interceptorMethods, step, interceptorClass);
	}

	@Override
	public void execute(InterceptorStack stack,
			ControllerMethod controllerMethod, Object currentController) {
		Object interceptor = container.instanceFor(interceptorClass);

		logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());

		if (acceptsExecutor.execute(interceptor)) {
			before.execute(interceptor);
			around.execute(interceptor);
			after.execute(interceptor);
		} else {
			stack.next(controllerMethod, currentController);
		}
	}

	@Override
	public String toString() {
		return "AspectStyleInterceptorHandler for " + interceptorClass.getName();
	}
}
