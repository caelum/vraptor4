package br.com.caelum.vrapto4.test;

import static br.com.caelum.vraptor4.view.Results.http;

import java.io.IOException;

import javax.inject.Inject;

import br.com.caelum.vraptor4.Controller;
import br.com.caelum.vraptor4.Result;

@Controller
public class SimpleController {

	@Inject
	private Result result;

	public void test() throws IOException{
		result.use(http()).body("<html><body>Olha olha ai</body></html>");
	}
}
