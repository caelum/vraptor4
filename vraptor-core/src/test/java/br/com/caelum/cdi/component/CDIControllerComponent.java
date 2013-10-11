package br.com.caelum.cdi.component;

import br.com.caelum.vraptor.Controller;

@Controller
public class CDIControllerComponent {

	private boolean initializedDepencies;

	//CDI eyes only
	@Deprecated
	public CDIControllerComponent() {
	}
	
	public CDIControllerComponent(CDIComponent component) {
		this.initializedDepencies = true;
	}

	public boolean isInitializedDepencies() {
		return initializedDepencies;
	}
	
	
}
