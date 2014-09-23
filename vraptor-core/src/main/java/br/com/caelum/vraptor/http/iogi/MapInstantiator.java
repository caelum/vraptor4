package br.com.caelum.vraptor.http.iogi;

import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

/**
 * An instantiator that supports Maps.
 *
 * @author Otávio Scherer Garcia
 * @since 4.1
 */
@Vetoed
public class MapInstantiator
	implements Instantiator<Map<Object, Object>> {

	public MapInstantiator(VRaptorInstantiator instantiator) {
	}

	@Override
	public Map<Object, Object> instantiate(Target<?> target, Parameters parameters) {
		List<Parameter> params = parameters.forTarget(target);
		if (params.isEmpty()) {
			return emptyMap();
		}

		String key = getParameterKey(params);

		if (params.get(0).isDecorated()) {
			List<Object> values = new ArrayList<>();
			for (Parameter value : params) {
				values.add(value.getValue());
			}
			return createSingletonMap(key, values);
		}

		return createSingletonMap(key, params.get(0).getValue());
	}

	private Map<Object, Object> createSingletonMap(String key, Object value) {
		return Collections.<Object, Object> singletonMap(key, value);
	}

	/**
	 * Return the parameter decoration. Example: for <code>map[name]</code> returns <code>name</code>.
	 */
	private String getParameterKey(List<Parameter> params) {
		String key = params.get(0).getFirstNameComponentWithDecoration();
		return key.substring(key.indexOf("[") + 1, key.indexOf("]"));
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType().isAssignableFrom(Map.class);
	}
}
