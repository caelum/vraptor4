package br.com.caelum.vrapto4.test;

import javax.inject.Inject;

import br.com.caelum.vraptor4.Controller;
import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.interceptor.IncludeParameters;

@Controller
public class ProfileController {

	@Inject private Result result;

	@IncludeParameters
	public void boraLa(Profile profile){
	}
}
