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
package br.com.caelum.vraptor.proxy;

import javax.enterprise.inject.Vetoed;

import com.thoughtworks.xstream.InitializationException;
import org.jboss.weld.bean.proxy.ProxyObject;
import org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy;

/**
 * Utility class to work with CDI proxies, allowing us to get objects without weld proxies. At this time this
 * class only works with JBoss Weld, but since Weld is the only one that implements CDI 1.1, this is not a big
 * trouble.
 * 
 * @author Otávio Scherer Garcia
 * @author Mario Amaral
 */
@Vetoed
public final class CDIProxies {
	private CDIProxies(){
		throw new InitializationException("Not allowed to initialize");
	}

	public static boolean isCDIProxy(Class<?> type) {
		return ProxyObject.class.isAssignableFrom(type);
	}

	public static <T> Class<?> extractRawTypeIfPossible(Class<T> type) {
		return isCDIProxy(type) ? type.getSuperclass() : type;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T unproxifyIfPossible(T source) {
		if (source instanceof TargetInstanceProxy) {
			TargetInstanceProxy<T> target = (TargetInstanceProxy) source;
			return target.getTargetInstance();
		}
		return source;
	}
}
