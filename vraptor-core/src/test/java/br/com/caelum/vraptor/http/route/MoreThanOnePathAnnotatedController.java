package br.com.caelum.vraptor.http.route;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;

@Controller
@Path({"/prefix", "/prefix2"})
@Vetoed
public class MoreThanOnePathAnnotatedController {
	public void noSlashPath() {
	}
}