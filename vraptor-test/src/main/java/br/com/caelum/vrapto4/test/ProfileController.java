package br.com.caelum.vrapto4.test;

import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor4.Controller;

@Controller
public class ProfileController {

	@Inject private Result result;
	
	public void boraLa(Profile profile){
		result.include("name", profile.getName());
		result.include("email", profile.getEmail());
	}
}
