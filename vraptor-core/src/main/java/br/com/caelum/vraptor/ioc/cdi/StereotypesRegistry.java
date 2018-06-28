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
package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.core.ControllerQualifier;
import br.com.caelum.vraptor.core.ConvertQualifier;
import br.com.caelum.vraptor.core.DeserializesQualifier;
import br.com.caelum.vraptor.core.InterceptorStackHandlersCache;
import br.com.caelum.vraptor.core.InterceptsQualifier;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.serialization.Deserializes;

import com.google.common.collect.ImmutableMap;

@Dependent
public class StereotypesRegistry {

	private static final Map<Class<?>, Annotation> STEREOTYPES;

	@Inject private BeanManager beanManager;
	@Inject private InterceptorStackHandlersCache interceptorsCache;

	public void configure(@Observes VRaptorInitialized event){
		for (Bean<?> bean : beanManager.getBeans(Object.class)) {
			Annotation qualifier = tryToFindAStereotypeQualifier(bean);
			if (qualifier != null) {
				beanManager.fireEvent(new DefaultBeanClass(bean.getBeanClass()), qualifier);
			}
		}
		interceptorsCache.init();
	}

	private Annotation tryToFindAStereotypeQualifier(Bean<?> bean) {
		for (Class<? extends Annotation> annotation : bean.getStereotypes()) {
			if (STEREOTYPES.containsKey(annotation)) {
				return STEREOTYPES.get(annotation);
			}
		}
		return null;
	}

	static {
		STEREOTYPES = ImmutableMap.<Class<?>, Annotation>of(
			Controller.class, new AnnotationLiteral<ControllerQualifier>() {},
			Convert.class, new AnnotationLiteral<ConvertQualifier>() {},
			Deserializes.class, new AnnotationLiteral<DeserializesQualifier>() {},
			Intercepts.class, new AnnotationLiteral<InterceptsQualifier>(){}
		);
	}
}
