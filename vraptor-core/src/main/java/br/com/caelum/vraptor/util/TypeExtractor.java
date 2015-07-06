package br.com.caelum.vraptor.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.enterprise.inject.Vetoed;

/**
 * Extract and resolve type of a generic parameter
 * 
 * @author Fábio Franco
 */
@Vetoed
public class TypeExtractor {

	private Class<?> cls;

	public TypeExtractor(Class<?> cls) {
		this.cls = cls;
	}

	private Type resolveTypeVariable(TypeVariable<?> type) {
		ParameterizedType parameterizedType = (ParameterizedType) cls.getGenericSuperclass();
		Class<?> rawType = (Class<?>) parameterizedType.getRawType();
		TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
		if (typeParameters.length > 0) {
			for (int i = 0; i < typeParameters.length; i++) {
				TypeVariable<?> typeVariable = typeParameters[i];
				if (typeVariable.getName().equals(type.getName())) {
					return (Class<?>) parameterizedType.getActualTypeArguments()[i];
				}
			}
		}
		return type;
	}

	public Type extractType(Type type) {
		if (type instanceof TypeVariable) {
			return resolveTypeVariable((TypeVariable<?>) type);
		}
		return type;
	}

}
