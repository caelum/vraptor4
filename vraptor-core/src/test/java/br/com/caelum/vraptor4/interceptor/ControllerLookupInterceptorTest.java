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
package br.com.caelum.vraptor4.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.core.MethodInfo;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.http.MutableResponse;
import br.com.caelum.vraptor4.http.UrlToControllerTranslator;
import br.com.caelum.vraptor4.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor4.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor4.interceptor.ControllerLookupInterceptor;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor4.restfulie.controller.HttpMethod;
import br.com.caelum.vraptor4.restfulie.controller.MethodNotAllowedHandler;

public class ControllerLookupInterceptorTest {

    private @Mock UrlToControllerTranslator translator;
    private @Mock MutableRequest webRequest;
    private @Mock MutableResponse webResponse;
    private @Mock RequestInfo info;
    private ControllerLookupInterceptor lookup;
    private @Mock MethodInfo methodInfo;
	private @Mock ControllerNotFoundHandler notFoundHandler;
	private @Mock MethodNotAllowedHandler methodNotAllowedHandler;

    @Before
    public void config() {
    	MockitoAnnotations.initMocks(this);
        info = new RequestInfo(null, null, webRequest, webResponse);
        lookup = new ControllerLookupInterceptor(translator, methodInfo, notFoundHandler, methodNotAllowedHandler, info);
    }
    
    @Test
    public void shouldAcceptAlways() {
    	assertTrue(lookup.accepts(null));
    }

    @Test
    public void shouldHandle404() throws IOException, InterceptionException {
        when(translator.translate(info)).thenThrow(new ControllerNotFoundException());
                
        lookup.intercept(null, null, null);
        verify(notFoundHandler).couldntFind(info);
    }

    @Test
    public void shouldHandle405() throws IOException, InterceptionException {
    	EnumSet<HttpMethod> allowedMethods = EnumSet.of(HttpMethod.GET);
    	
        when(translator.translate(info)).thenThrow(new MethodNotAllowedException(allowedMethods, HttpMethod.POST.toString()));
                
        lookup.intercept(null, null, null);
        verify(methodNotAllowedHandler).deny(info, allowedMethods);
    }

    @Test
    public void shouldUseControllerMethodFoundWithNextInterceptor() throws IOException, InterceptionException {
        final ControllerMethod method = mock(ControllerMethod.class);
        final InterceptorStack stack = mock(InterceptorStack.class);
        
        when(translator.translate(info)).thenReturn(method);
        
        lookup.intercept(stack, null, null);
        verify(stack).next(method, null);
        verify(methodInfo).setControllerMethod(method);
        assertEquals(method, lookup.createControllerMethod());
    }
}
