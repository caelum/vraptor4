/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.vraptor.ioc.Container;

/**
 * an adapter for IOGI's dependency provider based on VRaptor's container
 *
 * @author Lucas Cavalcanti
 * @since
 *
 */
@ApplicationScoped
public class VRaptorDependencyProvider implements DependencyProvider {

	private final Container container;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected VRaptorDependencyProvider() {
		this(null);
	}

	@Inject
	public VRaptorDependencyProvider(Container container) {
		this.container = container;
	}
	
	@Override
	public boolean canProvide(Target<?> target) {
		return container.canProvide(target.getClassType());
	}

	@Override
	public Object provide(Target<?> target) {
		return container.instanceFor(target.getClassType());
	}
}
