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

package br.com.caelum.vraptor.http.route;

import java.util.ArrayList;
import java.util.List;

/**
 * Rules for controller localization.
 *
 * By default, Routes added by this class will have higher priority (will have
 * lower value of priority), so will be tested first at Router.parse method.
 *
 * @author Guilherme Silveira
 */
public abstract class Rules {
	private final Router router;
	private final List<RouteBuilder> routesToBuild = new ArrayList<>();

	public Rules(Router router) {
		this.router = router;
		routes();
		for(RouteBuilder builder : routesToBuild) {
			router.add(builder.build());
		}
	}

	public abstract void routes();

	protected final RouteBuilder routeFor(String uri) {
		RouteBuilder rule = router.builderFor(uri);
		rule.withPriority(Integer.MIN_VALUE);
		this.routesToBuild.add(rule);
		return rule;
	}
}
