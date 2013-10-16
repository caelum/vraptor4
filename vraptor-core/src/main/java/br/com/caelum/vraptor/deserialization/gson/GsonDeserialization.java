package br.com.caelum.vraptor.deserialization.gson;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.view.ResultException;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A GSON deserializer.
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@Deserializes({ "application/json", "json" })
public class GsonDeserialization implements Deserializer {

	private static final Logger logger = LoggerFactory.getLogger(GsonDeserialization.class);

	private final ParameterNameProvider paramNameProvider;
	private final Instance<JsonDeserializer<?>> adapters; 
	private final HttpServletRequest request;

	/** @Deprecated CDI eyes only */
	protected GsonDeserialization() {
		this(null, null, null);
	}
	
	@Inject
	public GsonDeserialization(ParameterNameProvider paramNameProvider, @Any Instance<JsonDeserializer<?>> adapters, 
			HttpServletRequest request) {
		this.paramNameProvider = paramNameProvider;
		this.adapters = adapters;
		this.request = request;
	}

	@Override
	public Object[] deserialize(InputStream inputStream, ControllerMethod method) {
		Class<?>[] types = getTypes(method);
		Type[] pTypes = method.getMethod().getGenericParameterTypes();
		
		if (types.length == 0) {
			throw new IllegalArgumentException("Methods that consumes representations must receive just one argument");
		}

		Gson gson = getGson();
		
		Object[] params = new Object[types.length];
		String[] parameterNames = paramNameProvider.parameterNamesFor(method.getMethod());

		try {
			String content = getContentOfStream(inputStream);
			logger.debug("json retrieved: {}", content);
			
			if (!isNullOrEmpty(content)) {
				JsonParser parser = new JsonParser();
				JsonElement jsonElement = parser.parse(content);
				if (jsonElement.isJsonObject()) {
					JsonObject root = jsonElement.getAsJsonObject();
		
					for (int i = 0; i < types.length; i++) {
						String name = parameterNames[i];
						JsonElement node = root.get(name);
						
						if (isWithoutRoot(parameterNames, root)) { 
							params[i] = gson.fromJson(root, types[i]);
							logger.info("json without root deserialized");
							break;

						} else if (node != null) {
							if (node.isJsonArray()) {
								JsonArray jsonArray= node.getAsJsonArray();
								if (pTypes[i] instanceof ParameterizedType)
									params[i] = gson.fromJson(jsonArray, pTypes[i]);
								else
									params[i] = gson.fromJson(jsonArray, types[i]);
							} else {
								params[i] = gson.fromJson(node, types[i]);
							}
						}

						logger.debug("json deserialized: {}", params[i]);
					}
				} else if (jsonElement.isJsonArray()) {
					if ((pTypes.length != 1) || (!(pTypes[0] instanceof ParameterizedType)))
						throw new IllegalArgumentException("Methods that consumes an array representation must receive only just one collection generic typed argument");

					JsonArray jsonArray= jsonElement.getAsJsonArray();
					
					params[0] = gson.fromJson(jsonArray, pTypes[0]);
					
					logger.debug("array json deserialized: {}", params[0]);
				} else {
					throw new IllegalArgumentException("This is an invalid or not supported json content");
				}
			}
		} catch (Exception e) {
			throw new ResultException("Unable to deserialize data", e);
		}

		return params;
	}

	protected Gson getGson() {
		GsonBuilder builder = new GsonBuilder();

		for (JsonDeserializer<?> adapter : adapters) {
			builder.registerTypeHierarchyAdapter(getAdapterType(adapter), adapter);
		}

		return builder.create();
	}

	private Class<?> getAdapterType(JsonDeserializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];

		if (actualType instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) actualType).getRawType();
		else
			return (Class<?>) actualType;
	}

	private String getContentOfStream(InputStream input) throws IOException {
		String charset = getRequestCharset();
		logger.debug("Using charset {}", charset);

		return CharStreams.toString(new InputStreamReader(input, charset));
	}

	private String getRequestCharset() {
		String charset = firstNonNull(request.getHeader("Accept-Charset"), "UTF-8");
		return charset.split(",")[0];
	}

	private boolean isWithoutRoot(String[] parameterNames, JsonObject root) {
		for (String parameterName : parameterNames) {
			if (root.get(parameterName) != null)
				return false;
		}
		return true;
	}

	protected Class<?>[] getTypes(ControllerMethod method) {
		Class<?>[] parameterTypes = method.getMethod().getParameterTypes();
		Type genericType = getGenericSuperClass(method);
		if (genericType != null) {
			return parseGenericParameters(parameterTypes, genericType);
		}

		return parameterTypes;
	}

	private Class<?>[] parseGenericParameters(Class<?>[] parameterTypes,
			Type genericType) {
		Class<?> type = (Class<?>) getGenericType(genericType);
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].isAssignableFrom(type)) {
				parameterTypes[i] = type;
			}
		}
		return parameterTypes;
	}

	private Type getGenericSuperClass(ControllerMethod method) {
		Type genericType = method.getController().getType().getGenericSuperclass();
		if (genericType instanceof ParameterizedType) {
			return genericType;
		}

		return null;
	}

	private Type getGenericType(Type type) {
		ParameterizedType paramType = (ParameterizedType) type;
		return paramType.getActualTypeArguments()[0];
	}
}
