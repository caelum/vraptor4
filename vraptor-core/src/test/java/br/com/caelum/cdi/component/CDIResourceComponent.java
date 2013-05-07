package br.com.caelum.cdi.component;

import br.com.caelum.vraptor.Resource;

@Resource
public class CDIResourceComponent {

	private CDIComponent component;
	private boolean initializedDepencies;

	//CDI eyes only
	@Deprecated
	public CDIResourceComponent() {
	}
	
	public CDIResourceComponent(CDIComponent component) {
		this.component = component;
		this.initializedDepencies = true;
	}

	public boolean isInitializedDepencies() {
		return initializedDepencies;
	}
	
	
}
