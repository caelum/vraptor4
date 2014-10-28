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
package br.com.caelum.vraptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

/**
 * Executes a method in the current call. You should use the {@code SimpleInterceptorStack#next()} to execute
 * the next interceptor stack. The target method must have only one parameter with {@link SimpleInterceptorStack} or 
 * {@link InterceptorStack} type, and you can't have more than one method annotated with {@link AroundCall} 
 * in the same class.
 * 
 * <code>
 * \@AroundCall
 * public void intercept(SimpleInterceptorStack stack) {
 *     System.out.println("Executing before");
 *     stack.next();
 *     System.out.println("Executing after");
 * }
 * </code>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AroundCall {
}
