package br.com.caelum.vraptor.musicjungle.config;

import javax.enterprise.inject.Specializes;

import br.com.caelum.vraptor4.interceptor.multipart.DefaultMultipartConfig;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;

@ApplicationScoped
@Specializes
public class CustomMultipartConfig extends DefaultMultipartConfig {

	@Override
	public long getSizeLimit() {
		return 50 * 1024 * 1024;
	}

}