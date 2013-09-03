package br.com.caelum.cdi.component;

import br.com.caelum.vraptor.Controller;

@Controller
public class CDIControllerComponent {

	private CDIComponent component;
	private boolean initializedDepencies;

	//CDI eyes only
	@Deprecated
	public CDIControllerComponent() {
	}
	
	public CDIControllerComponent(CDIComponent component) {
		this.component = component;
		this.initializedDepencies = true;
	}

	public boolean isInitializedDepencies() {
		return initializedDepencies;
	}
	
	
}
