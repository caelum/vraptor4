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

package br.com.caelum.vraptor.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * A registry filled with interceptors to intercept requests.<br/>
 * Interceptors are queried wether they want to intercept a request through
 * their accepts method.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@ApplicationScoped @Alternative
public class DefaultInterceptorRegistry implements InterceptorRegistry {

    private final List<Class<?>> interceptors = new ArrayList<Class<?>>();

    public void register(Class<?>... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public List<Class<?>> all() {
        return interceptors;
    }

}
