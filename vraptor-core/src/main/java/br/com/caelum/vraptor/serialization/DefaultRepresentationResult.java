/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.serialization;

import static br.com.caelum.vraptor.view.Results.status;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.FormatResolver;

/**
 * Default implementation for RepresentationResult that uses request Accept format to
 * decide which representation will be used
 * @author Lucas Cavalcanti
 * @author Jose Donizetti
 * @since 3.0.3
 */
@RequestScoped
public class DefaultRepresentationResult implements RepresentationResult {

	private final FormatResolver formatResolver;
	private final Iterable<Serialization> serializations;
	private final Result result;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultRepresentationResult() {
		this(null, null, null);
	}

	@Inject
	public DefaultRepresentationResult(FormatResolver formatResolver, Result result, @Any Instance<Serialization> serializations) {
		this.formatResolver = formatResolver;
		this.result = result;
		this.serializations = serializations;
	}

	@Override
	public <T> Serializer from(T object) {
		return from(object, null);
	}

	/**
	 * Override this method if you want another ordering strategy.
	 *
	 * @since 3.4.0
	 */
	protected void sortSerializations(List<Serialization> serializations) {
		sort(serializations, new ApplicationPackageFirst());
	}

	@Override
	public <T> Serializer from(T object, String alias) {
		if(object == null) {
			result.use(status()).notFound();
			return new IgnoringSerializer();
		}
		
		List<Serialization> serializations = newArrayList(this.serializations);
		sortSerializations(serializations);
		
		String format = formatResolver.getAcceptFormat();
		for (Serialization serialization : serializations) {
			if (serialization.accepts(format)) {
				if(alias==null) {
					return serialization.from(object);
				} else {
					return serialization.from(object, alias);
				}
			}
		}
		result.use(status()).notAcceptable();

		return new IgnoringSerializer();
	}

	/**
	 * Comparator that give more priority to application classes.
	 * @author A.C de Souza
	 * @since 3.4.0
	 */
	static final class ApplicationPackageFirst implements Comparator<Serialization>, Serializable {

		public static final long serialVersionUID = 1L;

		private static final String VRAPTOR_PACKAGE = "br.com.caelum.vraptor.serialization";

		private int priority(Serialization s) {
			return s.getClass().getPackage().getName().startsWith(VRAPTOR_PACKAGE) ? 1 : 0;
		}

		@Override
		public int compare(Serialization o1, Serialization o2) {
			return priority(o1) - priority(o2);
		}
	}
}
