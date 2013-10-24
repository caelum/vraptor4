package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class Invocation {
	private Class<?> controllerType;
	private Method method;

	public Invocation(Class<?> type, Method method) {
		controllerType = type;
		this.method = method;
	}

	@Override
	public int hashCode() {
		return Objects.hash(controllerType, method.getName())
				^ Arrays.hashCode(method.getParameterTypes());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		Invocation other = (Invocation) obj;
		return Objects.equals(controllerType, other.controllerType)
				&& Objects.equals(method.getName(), other.method.getName())
				&& Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes());
	}
}