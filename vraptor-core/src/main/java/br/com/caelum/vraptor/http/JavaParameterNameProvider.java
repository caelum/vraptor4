package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.cache.CacheStore;

@Vetoed
public class JavaParameterNameProvider
	implements ParameterNameProvider {

	private static final Logger logger = LoggerFactory.getLogger(JavaParameterNameProvider.class);

	private final CacheStore<AccessibleObject, Parameter[]> cache;

	public JavaParameterNameProvider(CacheStore<AccessibleObject, Parameter[]> cache) {
		this.cache = cache;
	}

	@Override
	public Parameter[] parametersFor(final AccessibleObject executable) {
		return cache.fetch(executable, new Callable<Parameter[]>() {
			@Override
			public Parameter[] call()
				throws Exception {
				logger.debug("looking for parameter names {}", executable);

				Object[] params = (Object[]) new Mirror().on((Object) executable).invoke().method("getParameters").withoutArgs();
				Parameter[] out = new Parameter[params.length];

				for (int i = 0; i < params.length; i++) {
					if (isParameterPresent(params[i])) {
						String name = (String) new Mirror().on(params[i]).invoke().method("getName").withoutArgs();
						out[i] = new Parameter(i, name, executable);
					} else {
						throw new IllegalStateException("No parameters found for method " + executable
								+ ". Make sure your code was compiled with -parameters option.");
					}
				}

				return out;
			}
		});
	}

	private boolean isParameterPresent(Object param) {
		return (Boolean) new Mirror().on(param).invoke().method("isNamePresent").withoutArgs();
	}
}
