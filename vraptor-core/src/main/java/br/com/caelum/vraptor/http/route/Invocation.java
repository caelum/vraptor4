package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Arrays;

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
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((controllerType == null) ? 0 : controllerType.hashCode());
		result = prime * result
				+ ((method == null) ? 0 : method.getName().hashCode());
		result = prime * result
				+ ((method == null) ? 0 : Arrays.hashCode(method.getParameterTypes()));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Invocation other = (Invocation) obj;
		if (controllerType == null) {
			if (other.controllerType != null)
				return false;
		} else if (!controllerType.equals(other.controllerType))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (method.getName().equals(other.method.getName())
				&& Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes()))
			return true;
		return false;
	}

}