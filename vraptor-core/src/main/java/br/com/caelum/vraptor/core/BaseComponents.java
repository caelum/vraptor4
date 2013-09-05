/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.core;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.deserialization.DeserializesHandler;
import br.com.caelum.vraptor.deserialization.XMLDeserializer;
import br.com.caelum.vraptor.ioc.ControllerHandler;
import br.com.caelum.vraptor.ioc.ConverterHandler;
import br.com.caelum.vraptor.ioc.InterceptorStereotypeHandler;

/**
 * List of base components to vraptor.<br/>
 * Those components should be available with any chosen ioc implementation.
 *
 * @author guilherme silveira
 */
public class BaseComponents {

    static final Logger logger = LoggerFactory.getLogger(BaseComponents.class);

	private static final HashMap<Class<? extends Annotation>, StereotypeInfo> STEREOTYPES_INFO = new HashMap<Class<? extends Annotation>,StereotypeInfo>();
    static {
    		STEREOTYPES_INFO.put(Controller.class,new StereotypeInfo(Controller.class,ControllerHandler.class,new AnnotationLiteral<ControllerQualifier>() {}));
    		STEREOTYPES_INFO.put(Convert.class,new StereotypeInfo(Convert.class,ConverterHandler.class,new AnnotationLiteral<ConvertQualifier>() {}));
    		STEREOTYPES_INFO.put(Deserializes.class,new StereotypeInfo(Deserializes.class,DeserializesHandler.class,new AnnotationLiteral<DeserializesQualifier>() {}));
    		STEREOTYPES_INFO.put(Intercepts.class,new StereotypeInfo(Intercepts.class,InterceptorStereotypeHandler.class,new AnnotationLiteral<InterceptsQualifier>() {}));

    }

    private static final Set<Class<? extends Deserializer>> DESERIALIZERS = Collections.<Class<? extends Deserializer>>singleton(XMLDeserializer.class);


    public static Set<Class<? extends Deserializer>> getDeserializers() {
		return DESERIALIZERS;
	}

    public static Set<StereotypeInfo> getStereotypesInfo() {
    		return new HashSet<StereotypeInfo>(STEREOTYPES_INFO.values());
    }

    public static Set<Class<? extends Annotation>> getStereotypes() {
    		Set<StereotypeInfo> stereotypesInfo = getStereotypesInfo();
    		HashSet<Class<? extends Annotation>> stereotypes = new HashSet<Class<? extends Annotation>>();
    		for (StereotypeInfo stereotypeInfo : stereotypesInfo) {
    			stereotypes.add(stereotypeInfo.getStereotype());
		}
    		return stereotypes;
    }
    public static Map<Class<? extends Annotation>,StereotypeInfo> getStereotypesInfoMap() {
    		return STEREOTYPES_INFO;
    }

    private static Map<Class<?>, Class<?>> classMap(Class<?>... items) {
        HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        Iterator<Class<?>> it = Arrays.asList(items).iterator();
        while (it.hasNext()) {
            Class<?> key = it.next();
            Class<?> value = it.next();
            if (value == null) {
                throw new IllegalArgumentException("The number of items should be even.");
            }
            map.put(key, value);
        }
        return map;
    }


}