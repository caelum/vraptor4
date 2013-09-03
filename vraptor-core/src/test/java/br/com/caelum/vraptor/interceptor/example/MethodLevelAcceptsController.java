package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.Controller;

@Controller
public class MethodLevelAcceptsController {

	@NotLogged
	public void home(){
		
	}
	
	public void notAllowed(){
		
	}
}
