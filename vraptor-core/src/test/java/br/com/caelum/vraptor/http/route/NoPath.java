package br.com.caelum.vraptor.http.route;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;

@Controller
@Vetoed
public class NoPath {

	@Path( {})
	public void noPaths() {
	}
}