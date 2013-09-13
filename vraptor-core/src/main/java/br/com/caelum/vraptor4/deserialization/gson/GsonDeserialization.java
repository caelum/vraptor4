package br.com.caelum.vraptor4.deserialization.gson;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.deserialization.Deserializer;
import br.com.caelum.vraptor4.deserialization.Deserializes;
import br.com.caelum.vraptor4.http.ParameterNameProvider;
import br.com.caelum.vraptor4.view.ResultException;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@Deserializes({ "application/json", "json" })
public class GsonDeserialization implements Deserializer {

	private static final Logger logger = LoggerFactory.getLogger(GsonDeserialization.class);

	private final ParameterNameProvider paramNameProvider;
	private final Collection<JsonDeserializer> adapters; 
	private final HttpServletRequest request;

	public GsonDeserialization(ParameterNameProvider paramNameProvider, List<JsonDeserializer> adapters, 
			HttpServletRequest request) {
		this.paramNameProvider = paramNameProvider;
		this.adapters = adapters;
		this.request = request;
	}

	public Object[] deserialize(InputStream inputStream, ControllerMethod method) {
		Method jMethod = method.getMethod();
		Class<?>[] types = jMethod.getParameterTypes();
		if (types.length == 0) {
			throw new IllegalArgumentException("Methods that consumes representations must receive just one argument");
		}

		Gson gson = getGson();

		Object[] params = new Object[types.length];
		String[] parameterNames = paramNameProvider.parameterNamesFor(jMethod);

		try {
			String content = getContentOfStream(inputStream);
			logger.debug("json retrieved: {}", content);
			
			if (!isNullOrEmpty(content)) {
				JsonParser parser = new JsonParser();
				JsonObject root = (JsonObject) parser.parse(content);
	
				for (int i = 0; i < types.length; i++) {
					String name = parameterNames[i];
					JsonElement node = root.get(name);
					
					if (isWithoutRoot(parameterNames, root)) { 
						params[i] = gson.fromJson(root, types[i]);
						logger.info("json without root deserialized");
						break;
						
					} else if (node != null) {
						params[i] = gson.fromJson(node, types[i]);
					}
					
					logger.debug("json deserialized: {}", params[i]);
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
}
