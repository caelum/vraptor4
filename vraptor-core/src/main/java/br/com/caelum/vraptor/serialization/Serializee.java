package br.com.caelum.vraptor.serialization;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Vetoed;

import net.vidageek.mirror.dsl.Mirror;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

@Vetoed
public class Serializee {
	private Object root;
	private Class<?> rootClass;
	private Multimap<String, Class<?>> includes;
	private Multimap<String, Class<?>> excludes;
	private Set<Class<?>> elementTypes;
	private boolean recursive;

	public Object getRoot() {
		return root;
	}

	public void setRoot(Object root) {
		this.root = root;
	}

	public Class<?> getRootClass() {
		return rootClass;
	}

	public void setRootClass(Class<?> rootClass) {
		this.rootClass = rootClass;
	}

	public Multimap<String, Class<?>> getIncludes() {
		if (includes == null) {
			includes = LinkedListMultimap.create();
		}
		
		return includes;
	}

	public Multimap<String, Class<?>> getExcludes() {
		if (excludes == null) {
			excludes = LinkedListMultimap.create();
		}
		
		return excludes;
	}

	public Set<Class<?>> getElementTypes() {
		return elementTypes;
	}

	public void setElementTypes(Set<Class<?>> elementTypes) {
		this.elementTypes = elementTypes;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public void excludeAll(String... names) {
		for (String name : names) {
			getExcludes().putAll(name.replaceAll("\\?", ""), getParentTypesFor(name));
		}
	}
	
	public void excludeAll() {
		Set<Class<?>> types = new HashSet<>();

		if (isCollection(getRootClass())) {
			types.addAll(getElementTypes());
		} else {
			types.add(getRootClass());
		}
		
		for (Class<?> type : types) {
			for (Field field : new Mirror().on(type).reflectAll().fields()) {
				getExcludes().putAll(field.getName(), getParentTypes(field.getName(), type)); 
			}
		}	
	}

	public void includeAll(String... names) {
		for (String name : names) {
			getIncludes().putAll(name.replaceAll("\\?", ""), getParentTypesFor(name));
		}
	}
	
	private Set<Class<?>> getParentTypesFor(String name) {
		if (getElementTypes() == null) {
			Class<?> type = getRootClass();
			return getParentTypes(name, type);
		} else {
			Set<Class<?>> result = new HashSet<>();
			for (Class<?> type : getElementTypes()) {
				result.addAll(getParentTypes(name, type));
			}
			return result;
		}
	}

	private Set<Class<?>> getParentTypes(String name, Class<?> type) {
		String[] path = name.split("\\.");
		
		try {
			for (int i = 0; i < path.length; i++) {
			    Field field = reflectField(path[i], type);
			    if (field == null) break;
			    if (i < path.length - 1) type = getActualType(field.getGenericType());
			}
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Field path '" + name + "' doesn't exists in " + type, e);
		}
		
		Set<Class<?>> types = new HashSet<>();
		while (type != Object.class) {
			types.add(type);
			type = type.getSuperclass();
		}
		return types;
	}
	
	private Field reflectField(String path, Class<?> type) {
		Field field = new Mirror().on(type).reflect().field(path.replaceAll("\\?", ""));
		if (!path.startsWith("?"))
			checkNotNull(field);
		return field;
	}

	private static Class<?> getActualType(Type genericType) {
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;

			if (isCollection(type)) {
				Type actualType = type.getActualTypeArguments()[0];

				if (actualType instanceof TypeVariable<?>) {
					return (Class<?>) type.getRawType();
				}

				return (Class<?>) actualType;
			}
		}

		return (Class<?>) genericType;
	}

	private static boolean isCollection(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) type;
			return Collection.class.isAssignableFrom((Class<?>) ptype.getRawType())
					|| Map.class.isAssignableFrom((Class<?>) ptype.getRawType());
		}
		return Collection.class.isAssignableFrom((Class<?>) type);
	}
}