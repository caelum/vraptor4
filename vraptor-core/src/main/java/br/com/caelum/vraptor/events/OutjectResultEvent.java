package br.com.caelum.vraptor.events;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.OutjectResult;

/**
 * Event fired by {@link ExecuteMethodInterceptor} when it has
 * fully completed it's process and reflected method contains a
 * non void generic return type. @see {@link OutjectResult}.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public class OutjectResultEvent {

	private Type genericReturnType;

	public OutjectResultEvent(Type genericReturnType) {
		this.genericReturnType = genericReturnType;
	}

	public Type getGenericReturnType() {
		return genericReturnType;
	}
}