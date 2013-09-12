package br.com.caelum.vraptor.http.route;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;

@Controller
@Vetoed
public class WrongGetAnnotatedController {
	@Path("/some") @Get("/other")
	public void withAmbiguousDeclaration() {
	}
}