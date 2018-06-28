package br.com.caelum.vraptor.musicjungle.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import br.com.caelum.vraptor.observer.upload.DefaultMultipartConfig;

@ApplicationScoped
@Specializes
public class CustomMultipartConfig extends DefaultMultipartConfig {

	@Override
	public long getSizeLimit() {
		return 50 * 1024 * 1024;
	}

}
