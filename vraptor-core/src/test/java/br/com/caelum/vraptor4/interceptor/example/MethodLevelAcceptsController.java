package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Controller;

@Controller
public class MethodLevelAcceptsController {

	@NotLogged
	public void home(){
		
	}
	
	public void notAllowed(){
		
	}
}
