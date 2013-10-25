package br.com.caelum.vraptor.interceptor;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;

@Vetoed
public class AspectStyleInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = getLogger(AspectStyleInterceptorHandler.class);

	private final StepInvoker stepInvoker;
	private final Container container;
	private final Class<?> interceptorClass;
	private final InterceptorMethodParametersResolver parametersResolver;
	private StepExecutor<?> after;
	private StepExecutor<?> around;
	private StepExecutor<?> before;
	private MirrorList<Method> interceptorMethods;
	private SimpleInterceptorStack simpleInterceptorStack;
	private CustomAcceptsExecutor customAcceptsExecutor;
	private InterceptorAcceptsExecutor acceptsExecutor;

	public AspectStyleInterceptorHandler(Class<?> interceptorClass, StepInvoker stepInvoker,
			Container container, InterceptorMethodParametersResolver parametersResolver,
			SimpleInterceptorStack simpleInterceptorStack,
			CustomAcceptsExecutor customAcceptsExecutor, InterceptorAcceptsExecutor acceptsExecutor) {

		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;
		this.parametersResolver = parametersResolver;
		this.simpleInterceptorStack = simpleInterceptorStack;
		this.customAcceptsExecutor = customAcceptsExecutor;
		this.acceptsExecutor = acceptsExecutor;
		this.interceptorMethods = stepInvoker.findAllMethods(interceptorClass);
		configure();
	}

	private void configure() {

		after = new NoStackParameterStepExecutor(stepInvoker, find(AfterCall.class));
		around = new AroundExecutor(stepInvoker,parametersResolver, find(AroundCall.class));
		before = new NoStackParameterStepExecutor(stepInvoker, find(BeforeCall.class));

		if(!after.accept()) after = new DoNothingStepExecutor();
		if(!around.accept()) around = new StackNextExecutor(simpleInterceptorStack);
		if(!before.accept()) before = new DoNothingStepExecutor();
	}

	@Override
	public void execute(InterceptorStack stack,
			ControllerMethod controllerMethod, Object currentController) {

		Object interceptor = container.instanceFor(interceptorClass);

		logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());

		if (customAccepts(interceptor) || internalAccepts(interceptor)) {
			before.execute(interceptor);
			around.execute(interceptor);
			after.execute(interceptor);
		} else {
			stack.next(controllerMethod, currentController);
		}
	}

	private Method find(Class<? extends Annotation> step) {
		return stepInvoker.findMethod(interceptorMethods, step, interceptorClass);
	}

	private boolean internalAccepts(Object interceptor) {
		return acceptsExecutor.accepts(interceptor, find(Accepts.class));
	}

	private boolean customAccepts(Object interceptor) {
		return customAcceptsExecutor.accepts(interceptor, find(CustomAcceptsFailCallback.class));
	}

	@Override
	public String toString() {
		return "AspectStyleInterceptorHandler for " + interceptorClass.getName();
	}
}